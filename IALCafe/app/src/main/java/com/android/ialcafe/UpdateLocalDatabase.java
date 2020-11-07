package com.android.ialcafe;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import androidx.appcompat.app.AlertDialog;

public abstract class UpdateLocalDatabase {

    private String urlCanteen = CommonUtils.IP + "/ialcafe/canteen_json.php";
    private String urlItemMaster = CommonUtils.IP + "/ialcafe/item_master_json.php";
    private String urlEmployeeMaster = CommonUtils.IP + "/ialcafe/employee_master_json.php";
    private String urlCanteenMenu = CommonUtils.IP + "/ialcafe/canteen_menu_json.php";

    Context context;
    DataBaseHelper myDatabase;
    Boolean[] progress = new Boolean[4];

    int progressStatus = 0;
    private Handler handler = new Handler();
    JsonObject jsonObject = new JsonObject();

    public UpdateLocalDatabase(Context context){
        this.context = context;

        myDatabase = new DataBaseHelper(context);
    }

    public void checkServerAvailability(int time) {
        AsyncCheckAvailability asyncCheckAvailability = new AsyncCheckAvailability();
        asyncCheckAvailability.execute(String.valueOf(time));
    }

    public void updateAll(){
        Arrays.fill(progress, false);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("IAL Cafe");
        alertDialogBuilder.setMessage("Updating Local Database...");

        LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.progress_dialogs, null);
        alertDialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        final ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgressDrawable(progressDrawable);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                boolean isProgress = true;
                boolean isUpdateSuccessful = true;
                progressBar.setProgress(progressStatus);
                while(i < 4){
                    try {
                        if (isUpdateSuccessful){
                            switch (i) {
                                case 0:
                                    PostCanteenTable postCanteenTable = new PostCanteenTable(context);
                                    postCanteenTable.checkServerAvailability(0);
                                    break;
                                case 1:
                                    PostCanteenMenuTable postCanteenMenuTable = new PostCanteenMenuTable(context);
                                    postCanteenMenuTable.checkServerAvailability(0);
                                    break;
                                case 2:
                                    PostEmployeeMasterTable postEmployeeMasterTable = new PostEmployeeMasterTable(context);
                                    postEmployeeMasterTable.checkServerAvailability(0);
                                    break;
                                case 3:
                                    PostItemMasterTable postItemMasterTable = new PostItemMasterTable(context);
                                    postItemMasterTable.checkServerAvailability(0);
                                    break;
                                default:
                                    break;
                            }
                            isUpdateSuccessful = false;
                        }

                        if (progress[i]) {
                            if(isProgress){
                                progressStatus += 25;
                                isProgress = false;
                            }else{
                                progressStatus += 25;
                                isProgress = true;
                            }
                            progressBar.setProgress(progressStatus);
                            isUpdateSuccessful = true;
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

    }

    private class PostCanteenTable extends PostRequest{
        public PostCanteenTable(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                myDatabase.deleteCanteenTable();
                super.postRequest(urlCanteen, new JsonObject());
            }else{
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
            }
        }

        public void onFinish(JSONArray jsonArray){
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                    int id = 0;
                    String menu = " ";
                    String menuName = " ";
                    String startTime = " ";
                    String endTime = " ";
                    String createdBy = " ";
                    String createdDate = " ";

                    if(jsonObject.has("id")) id = jsonObject.getInt("id");
                    if(jsonObject.has("menu")) menu = jsonObject.getString("menu");
                    if(jsonObject.has("menu_name")) menuName = jsonObject.getString("menu_name");
                    if(jsonObject.has("start_time")) startTime = jsonObject.getString("start_time");
                    if(jsonObject.has("end_time")) endTime = jsonObject.getString("end_time");
                    if(jsonObject.has("created_by")) createdBy = jsonObject.getString("created_by");
                    if(jsonObject.has("created_date")) createdDate = jsonObject.getString("created_date");

                    myDatabase.insertCanteenTable(id, menu, menuName, startTime, endTime, createdBy, createdDate);

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            progress[0] = true;
        }
    }

    private class PostCanteenMenuTable extends PostRequest{
        public PostCanteenMenuTable(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                myDatabase.deleteCanteenMenuTable();
                super.postRequest(urlCanteenMenu, new JsonObject());
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
            }
        }

        public void onFinish(JSONArray jsonArray){
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                    int id = 0;
                    int itemId = 0;
                    int canteenId = 0;
                    int iniMax = 0;
                    String createdBy = " ";
                    String createdOn = " ";
                    int status = 0;

                    if(jsonObject.has("id")) id = jsonObject.getInt("id");
                    if(jsonObject.has("item_id")) itemId = jsonObject.getInt("item_id");
                    if(jsonObject.has("canteen_id")) canteenId = jsonObject.getInt("canteen_id");
                    if(jsonObject.has("ini_max")) iniMax = jsonObject.getInt("ini_max");
                    if(jsonObject.has("created_by")) createdBy = jsonObject.getString("created_by");
                    if(jsonObject.has("created_on")) createdOn = jsonObject.getString("created_on");
                    if(jsonObject.has("status")) status = jsonObject.getInt("status");

                    myDatabase.insertCanteenMenuTable(id, itemId, canteenId, iniMax, createdBy, createdOn, status);

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            progress[1] = true;
        }
    }

    private class PostEmployeeMasterTable extends PostRequest{
        public PostEmployeeMasterTable(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                myDatabase.deleteEmployeeMasterTable();
                super.postRequest(urlEmployeeMaster, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
            }
        }

        public void onFinish(JSONArray jsonArray){
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONArray employee = (JSONArray) jsonArray.get(i);

                    int id = 0;
                    String empCode = " ";
                    String empName = " ";
                    String image = " ";
                    String shift = " ";
                    String access = " ";
                    int vegNonVeg = 0;
                    int nonVegCount = 0;
                    String department = " ";
                    String categoryId = " ";
                    String designation = " ";
                    int guestPermit = 0;
                    String createdBy = " ";
                    String createdOn = " ";
                    String reportingTo = " ";
                    String doj = " ";
                    String dob = " ";
                    String company = " ";
                    String rfidCard = " ";
                    int aRfidCard = 0;
                    String fromDate = " ";
                    String toDate = " ";
                    String email = " ";

                    if(!employee.get(0).toString().isEmpty()) id = Integer.parseInt(employee.get(0).toString());
                    if(!employee.get(1).toString().isEmpty()) empCode = employee.get(1).toString();
                    if(!employee.get(2).toString().isEmpty()) empName = employee.get(2).toString();
                    if(!employee.get(3).toString().isEmpty()) image = employee.get(3).toString();
                    if(!employee.get(4).toString().isEmpty()) shift = employee.get(4).toString();
                    if(!employee.get(5).toString().isEmpty()) access = employee.get(5).toString();
                    if(!employee.get(6).toString().isEmpty() && !employee.get(6).toString().equals("null")) vegNonVeg = Integer.parseInt(employee.get(6).toString());
                    if(!employee.get(7).toString().isEmpty() && !employee.get(7).toString().equals("null")) nonVegCount = Integer.parseInt(employee.get(7).toString());
                    if(!employee.get(8).toString().isEmpty()) department = employee.get(8).toString();
                    if(!employee.get(9).toString().isEmpty()) categoryId = employee.get(9).toString();
                    if(!employee.get(10).toString().isEmpty()) designation = employee.get(10).toString();
                    if(!employee.get(11).toString().isEmpty() && !employee.get(11).toString().equals("null")) guestPermit = Integer.parseInt(employee.get(11).toString());
                    if(!employee.get(12).toString().isEmpty()) createdBy = employee.get(12).toString();
                    if(!employee.get(13).toString().isEmpty()) createdOn = employee.get(13).toString();
                    if(!employee.get(14).toString().isEmpty()) reportingTo = employee.get(14).toString();
                    if(!employee.get(15).toString().isEmpty()) doj = employee.get(15).toString();
                    if(!employee.get(16).toString().isEmpty()) dob = employee.get(16).toString();
                    if(!employee.get(17).toString().isEmpty()) company = employee.get(17).toString();
                    if(!employee.get(18).toString().isEmpty()) rfidCard = employee.get(18).toString();
                    if(!employee.get(19).toString().isEmpty() && !employee.get(19).toString().equals("null")) aRfidCard = Integer.parseInt(employee.get(19).toString());
                    if(!employee.get(20).toString().isEmpty()) fromDate = employee.get(20).toString();
                    if(!employee.get(21).toString().isEmpty()) toDate = employee.get(21).toString();
                    if(!employee.get(22).toString().isEmpty()) email = employee.get(22).toString();

                    myDatabase.insertEmployeeMasterTable(id, empCode, empName, image, shift, access, vegNonVeg, nonVegCount, department, categoryId, designation, guestPermit, createdBy, createdOn, reportingTo, doj, dob, company, rfidCard, aRfidCard, fromDate, toDate, email);

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            progress[2] = true;
        }
    }

    private class PostItemMasterTable extends PostRequest{
        public PostItemMasterTable(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                myDatabase.deleteItemMasterTable();
                super.postRequest(urlItemMaster, new JsonObject());
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
            }
        }

        public void onFinish(JSONArray jsonArray){
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                    int itemId = 0;
                    String itemName = " ";
                    Double amount = 0.0;
                    Double employeeAmount = 0.0;
                    Double companyAmount = 0.0;
                    Double contractorAmount = 0.0;
                    String image = " ";
                    int menu = 0;
                    int addMin = 0;
                    int shift = 0;
                    int status = 0;
                    String createdBy = " ";
                    String createdOn = " ";
                    int dropCount = 0;

                    if(jsonObject.has("item_id")) itemId = jsonObject.getInt("item_id");
                    if(jsonObject.has("item_name")) itemName = jsonObject.getString("item_name");
                    if(jsonObject.has("amount")) amount = jsonObject.getDouble("amount");
                    if(jsonObject.has("employee_amount")) employeeAmount = jsonObject.getDouble("employee_amount");
                    if(jsonObject.has("company_amount")) companyAmount = jsonObject.getDouble("company_amount");
                    if(jsonObject.has("contractor_amount")) contractorAmount = jsonObject.getDouble("contractor_amount");
                    if(jsonObject.has("image")) image = jsonObject.getString("image");
                    if(jsonObject.has("menu")) menu = jsonObject.getInt("menu");
                    if(jsonObject.has("add_min")) addMin = jsonObject.getInt("add_min");
                    if(jsonObject.has("shift")) shift = jsonObject.getInt("shift");
                    if(jsonObject.has("status")) status = jsonObject.getInt("status");
                    if(jsonObject.has("created_by")) createdBy = jsonObject.getString("created_by");
                    if(jsonObject.has("created_on")) createdOn = jsonObject.getString("created_on");
                    if(jsonObject.has("drop_count")) dropCount = jsonObject.getInt("drop_count");

                    myDatabase.insertItemMasterTable(itemId, itemName, amount, employeeAmount, companyAmount, contractorAmount, image, menu, addMin, shift, status, createdBy, createdOn, dropCount);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            progress[3] = true;
        }
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

    public abstract void serverAvailability(boolean isServerAvailable);

    public abstract void onPostUpdate();

    protected void finalize(){
        myDatabase.close();
    }

}