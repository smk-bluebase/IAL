package com.android.ialcafe;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;

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
import java.util.Iterator;


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
