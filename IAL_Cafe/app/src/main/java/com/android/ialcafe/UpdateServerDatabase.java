package com.android.ialcafe;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;

import androidx.appcompat.app.AlertDialog;

public abstract class UpdateServerDatabase {

    private String urlInsertInvoiceHeader = CommonUtils.IP + "/ialcafe/insertInvoiceHeader.php";

    Context context;
    SQLiteDatabase myDatabase;
    Boolean[] progress;
    int progressItems = 0;
    private Handler handler = new Handler();

    public UpdateServerDatabase(Context context) {
        this.context = context;

        myDatabase = context.openOrCreateDatabase("ial.sqlite", context.MODE_ENABLE_WRITE_AHEAD_LOGGING,null);;
    }

    public void checkServerAvailability(int time) {
        AsyncCheckAvailability asyncCheckAvailability = new AsyncCheckAvailability();
        asyncCheckAvailability.execute(String.valueOf(time));
    }

    private String urlEncode(String key, String value){
        String encodedURL = "";
        try{
            encodedURL = URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
        }catch(Exception e){
            e.printStackTrace();
        }
        return encodedURL;
    }

    public void updateTableInvoiceHeader(JsonObject jsonObject, boolean isSync){
        String data = "";

        try {
            JSONObject jsonData = new JSONObject(jsonObject.toString());
            Iterator<String> keys = jsonData.keys();

            while(keys.hasNext()){
                String key = keys.next();
                data += urlEncode(key, jsonData.get(key).toString()) + "&";
            }

            data = data.substring(0, data.length() - 1);
        }catch(Exception e) {
            e.printStackTrace();
        }

        AsyncPostRequest asyncPostRequest = new AsyncPostRequest();
        asyncPostRequest.execute(data, String.valueOf(isSync));

        System.out.println("data : " + data);
    }

    public void uploadToServer(){
        final JsonObject entries = new JsonObject();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("IAL Cafe");
        alertDialogBuilder.setMessage("Upload to Server Database......");

        LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.progress_dialogs, null);
        alertDialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        final ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgressDrawable(progressDrawable);

        progressBar.setProgress(0);

        try{
            Cursor resultSet = myDatabase.rawQuery(String.format("SELECT * FROM INVOICE_HEADER_SYNC"), null);
            resultSet.moveToFirst();

            int i = 0;
            while(i < resultSet.getCount()){
                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("coupon_no", resultSet.getString(1));
                jsonObject.addProperty("item_id", resultSet.getString(2));
                jsonObject.addProperty("guest_id", resultSet.getString(3));
                jsonObject.addProperty("no_of_person", resultSet.getString(4));
                jsonObject.addProperty("quanty", resultSet.getString(5));
                jsonObject.addProperty("amount", resultSet.getString(6));
                jsonObject.addProperty("emp_code", resultSet.getString(7));
                jsonObject.addProperty("rfid_card", resultSet.getString(8));
                jsonObject.addProperty("device_name", resultSet.getString(9));
                jsonObject.addProperty("device", resultSet.getString(10));
                jsonObject.addProperty("category_id", resultSet.getString(11));
                jsonObject.addProperty("otp_code", resultSet.getString(12));
                jsonObject.addProperty("date", resultSet.getString(16));
                jsonObject.addProperty("menu", resultSet.getString(14));
                jsonObject.addProperty("shift", resultSet.getString(15));
                jsonObject.addProperty("transaction_date", resultSet.getString(16));
                jsonObject.addProperty("closed_time", resultSet.getString(17));
                jsonObject.addProperty("status", resultSet.getString(18));
                jsonObject.addProperty("flag", resultSet.getString(19));
                jsonObject.addProperty("created_by", resultSet.getString(20));

                entries.add(String.valueOf(i), jsonObject);

                progressBar.setProgress(50);

                resultSet.moveToNext();
                i++;
            }

            resultSet.close();

            DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
            dataBaseHelper.deleteInvoiceHeaderSyncTable();
            dataBaseHelper.close();

            progress = new Boolean[i];
            Arrays.fill(progress, false);

            int j = 0;
            while(j < i){
                updateTableInvoiceHeader((JsonObject) entries.get(String.valueOf(j)), true);
                j++;
            }

            new Thread(new Runnable() {
                int i = 0;
                int progressStatus = 50;
                @Override
                public void run() {
                    while(i < progress.length){
                        try{
                            if (progress[i]) {
                                progressStatus += (int) 50/progress.length;
                                progressBar.setProgress(progressStatus);
                                i++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    alertDialog.dismiss();
                    handler.post(new Runnable() {
                        public void run() {
                            onPostUpdate();
                        }
                    });
                }
            }).start();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private class AsyncCheckAvailability extends AsyncTask<String, Boolean, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL(CommonUtils.IP);
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    int time = Integer.parseInt(strings[0]);
                    urlc.setConnectTimeout(time * 1000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isServerAvailable) {
            serverAvailability(isServerAvailable);
        }

    }

    private class AsyncPostRequest extends AsyncTask<String, Boolean, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                URL url = new URL(urlInsertInvoiceHeader);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                String data = strings[0];
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = reader.readLine()) != null){
                    sb.append(line + "\n");
                }

                String response = sb.toString();

                reader.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            return Boolean.valueOf(strings[1]);
        }

        @Override
        protected void onPostExecute(Boolean isSync) {
            if(isSync){
                progress[progressItems] = true;
                progressItems++;
            }
        }
    }

    public abstract void serverAvailability(boolean isServerAvailable);

    public abstract void onPostUpdate();

    protected void finalize(){
        myDatabase.close();
    }

}
