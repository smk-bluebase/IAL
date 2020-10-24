package com.android.ialcafe;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
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

public abstract class PostRequest {

    Context context;

    public PostRequest(Context context){
        this.context = context;
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

    public void postRequest(String url, JsonObject jsonObject){
        String data = "";

        try {
            JSONObject jsonData = new JSONObject(jsonObject.toString());
            Iterator<String> keys = jsonData.keys();

            while(keys.hasNext()){
                String key = keys.next();
                data += urlEncode(key, jsonData.get(key).toString()) + "&";
            }

            data = data.substring(0, data.length() - 1);
        }catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("data : " + data);

        AsyncPostRequest asyncPostRequest = new AsyncPostRequest();
        asyncPostRequest.execute(url, data);
    }

    private class AsyncCheckAvailability extends AsyncTask<String, Boolean, Boolean> {

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
                        Log.wtf("Connection", "Success !");
                        return true;
                    } else {
                        return false;
                    }
                } catch (MalformedURLException e1) {
                    return false;
                } catch (IOException e) {
                    return false;
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isServerAvailable) {
            serverAvailability(isServerAvailable);
        }

    }

    private class AsyncPostRequest extends AsyncTask<String, JSONArray, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            JSONArray  jsonArray = new JSONArray();

            try {
                URL url = new URL(strings[0]);
                System.out.println("url : " + strings[0]);

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                String data = strings[1];
                System.out.println("data : " + strings[1]);

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = reader.readLine()) != null){
                    sb.append(line + "\n");
                }

                jsonArray = new JSONArray(sb.toString());

                reader.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            onFinish(jsonArray);
        }
    }

    public abstract void serverAvailability(boolean isServerAvailable);

    public abstract void onFinish(JSONArray jsonArray);

    protected void finalize(){

    }
}
