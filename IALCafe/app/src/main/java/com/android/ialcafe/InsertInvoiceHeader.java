package com.android.ialcafe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

class InsertInvoiceHeader {
    Context context;
    UpdateRemoteDatabase updateRemoteDatabase;
    ProgressDialog progressDialog;

    String empCode;
    String rfidNo;
    String deviceName;
    String deviceModel;
    int categoryId;
    String todayDate;
    List<CanteenMenu> selectedItemList;

    public InsertInvoiceHeader(Context context){
        this.context = context;
        updateRemoteDatabase = new UpdateRemoteDatabase(this.context);
    }

    public void checkServerAvailability(int time){
        updateRemoteDatabase.checkServerAvailability(time);
    }

    public void setEmpCode(String empCode){
        this.empCode = empCode;
    }

    public void setRFIDNo(String rfidNo){
        this.rfidNo = rfidNo;
    }

    public void setDeviceName(String deviceName){
        this.deviceName = deviceName;
    }

    public void setDeviceModel(String deviceModel){
        this.deviceModel = deviceModel;
    }

    public void setCategoryId(int categoryId){
        this.categoryId = categoryId;
    }

    public void setTodayDate(String todayDate){
        this.todayDate = todayDate;
    }

    public void setSelectedItemList(List<CanteenMenu> selectedItemList){
        this.selectedItemList = selectedItemList;
    }

    public void insertToDatabase(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(c.getTime());

        Iterator i = selectedItemList.iterator();
        while(i.hasNext()) {
            CanteenMenu canteenMenu = (CanteenMenu) i.next();

            int couponNo = 0;
            int itemId = Integer.parseInt(canteenMenu.getMenuId());
            int guestId = 0;
            int noOfPerson = 1;
            int quantity = canteenMenu.getMenuQuantity();
            Float amount = canteenMenu.getMenuAmount() * canteenMenu.getMenuQuantity();
            int rfidCard = Integer.parseInt(rfidNo);
            String device = deviceModel;
            int otpCode = 0;
            String date = todayDate;
            String menu = canteenMenu.getMenuId();
            int shift = 1;
            String transactionDate = currentTime;
            String closedTime = currentTime;
            String status = "0";
            String flag = "1";
            String createdBy = "Android";
            String createdOn = currentTime;
            String updateStatus = "";
            int companyId = 1;
            int flagId = 1;
            String guestName = "";

            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("coupon_no", couponNo);
            jsonObject.addProperty("item_id",itemId);
            jsonObject.addProperty("guest_id", guestId);
            jsonObject.addProperty("no_of_person", noOfPerson);
            jsonObject.addProperty("quanty", quantity);
            jsonObject.addProperty("amount", amount);
            jsonObject.addProperty("emp_code", empCode);
            jsonObject.addProperty("rfid_card", rfidCard);
            jsonObject.addProperty("device_name", deviceName);
            jsonObject.addProperty("device", device);
            jsonObject.addProperty("category_id", categoryId);
            jsonObject.addProperty("otp_code", otpCode);
            jsonObject.addProperty("date", currentTime);
            jsonObject.addProperty("menu", menu);
            jsonObject.addProperty("shift", shift);
            jsonObject.addProperty("transaction_date", transactionDate);
            jsonObject.addProperty("closed_time", closedTime);
            jsonObject.addProperty("status", status);
            jsonObject.addProperty("flag", flag);
            jsonObject.addProperty("created_by", createdBy);
            jsonObject.addProperty("created_on", createdOn);

            try{
                DataBaseHelper myDatabase = new DataBaseHelper(context);

                myDatabase.insertInvoiceHeaderTable(couponNo, itemId, guestId, noOfPerson, quantity, amount, empCode, rfidCard, deviceName, device, categoryId, otpCode, date, menu, shift, transactionDate, closedTime, status, flag, createdBy, createdOn, updateStatus, companyId, flagId, guestName);

                myDatabase.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            updateRemoteDatabase.updateTableInvoiceHeader(jsonObject, false);
        }

        updateRemoteDatabase.onPostUpdate();
    }

    private class UpdateRemoteDatabase extends UpdateServerDatabase {
        public UpdateRemoteDatabase(Context context){
            super(context);
        }

        @Override
        public void serverAvailability(boolean isServerAvailable) {
            if(!isServerAvailable){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setTitle("IAL Cafe");
                alertDialogBuilder.setMessage("Connection to Server not Available!");
                alertDialogBuilder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                ((DisplayMenuActivity) context).finishAffinity();
                            }
                        });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }else {
                insertToDatabase();
            }
        }

        @Override
        public void onPostUpdate(){
            progressDialog.dismiss();
            Intent i = new Intent((DisplayMenuActivity) context, ScanActivity.class);
            i.putExtra("deviceName", deviceName);
            context.startActivity(i);
        }
    }

}
