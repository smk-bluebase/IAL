package com.android.ialcafe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

public class DisplayMenuActivity extends AppCompatActivity {

    public List<CanteenMenu> item_List = new ArrayList<>();
    public ListView listView;
    String deviceModel;

    String currentTime;
    String rfidNo;
    String deviceName;
    String empCode;
    String empName;
    int vegNonVeg;
    int guestPermit;
    int categoryId;
    String todayDate;
    int hour;
    int minute;
    int second;
    int rfid;

    Context context = this;
    List<CanteenMenu> selectedItemList = new ArrayList<>();
    String date;
    UpdateRemoteDatabase updateRemoteDatabase;

    JsonObject jsonObject;
    String urlCheckInvoiceHeader = CommonUtils.IP + "/ialcafe/check_invoice_header.php";
    int quantity = 0;
    String startTime = "";
    String endTime = "";

    boolean quantityGot = true;
    Handler handler = new Handler();
    boolean quantityEntry = false;

    int progressStatus = 0;
    boolean isMenuItemCheckAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaymenu);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.7);

        ImageView background = findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        deviceModel = Build.MODEL;

        Bundle retrieveIntent = getIntent().getExtras();
        date = retrieveIntent.getString("date");
        currentTime = retrieveIntent.getString("currentTime");
        rfidNo = retrieveIntent.getString("rfidNo");
        deviceName = retrieveIntent.getString("deviceName");
        empCode = retrieveIntent.getString("empCode");
        empName = retrieveIntent.getString("empName");
        vegNonVeg = Integer.parseInt(retrieveIntent.getString("vegNonVeg"));
        guestPermit = Integer.parseInt(retrieveIntent.getString("guestPermit"));
        categoryId = Integer.parseInt(retrieveIntent.getString("categoryId"));

        todayDate = new SimpleDateFormat("yyyy-MM-dd").format(Date.parse(date));
        hour = Integer.parseInt(currentTime.substring(0, currentTime.indexOf(":")));
        minute = Integer.parseInt(currentTime.substring(currentTime.indexOf(":") + 1, currentTime.lastIndexOf(":")));
        second = Integer.parseInt(currentTime.substring(currentTime.lastIndexOf(":") + 1));
        rfid = Integer.parseInt(rfidNo);

        listView = findViewById(R.id.list_vw);

        updateRemoteDatabase = new UpdateRemoteDatabase(context);

        CheckServer checkServer = new CheckServer(context);
        checkServer.checkServerAvailability(1);

        TextView empNameTextView = findViewById(R.id.empName);
        empNameTextView.setText(empName);

        TextView empCodeTextView = findViewById(R.id.empCode);
        empCodeTextView.setText(empCode);

        Button confirmIdButton = findViewById(R.id.submit);

        confirmIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMenuItemCheckAvailable = false;
                CheckServer checkServer = new CheckServer(context);
                checkServer.checkServerAvailability(1);
            }
        });

    }

    public void menu_display() {

        try{
            SQLiteDatabase myDatabase = openOrCreateDatabase("ial.sqlite", MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
            Cursor resultSet1 = myDatabase.rawQuery(String.format("SELECT MENU, START_TIME, END_TIME FROM CANTEEN WHERE \'%s\' >= start_time AND \'%s\' <= end_time", currentTime, currentTime), null);
            resultSet1.moveToFirst();

            if(resultSet1.getCount() > 0) {
                int availableMenuId = resultSet1.getInt(0);
                startTime = resultSet1.getString(1);
                endTime = resultSet1.getString(2);

                resultSet1.close();
//                Cursor resultSet2 = myDatabase.rawQuery(String.format("SELECT * FROM canteen_menu cm JOIN item_master i ON cm.item_id=i.item_id WHERE cm.canteen_id IN (3, 4) AND cm.status = 0 GROUP BY cm.item_id", availableMenuId), null);
                Cursor resultSet2 = myDatabase.rawQuery(String.format("SELECT * FROM canteen_menu cm JOIN item_master i ON cm.item_id=i.item_id WHERE cm.canteen_id = %d AND cm.status = 0", availableMenuId), null);
                resultSet2.moveToFirst();

                int i = 0;
                while (i < resultSet2.getCount()) {
                    int iniMax = resultSet2.getInt(resultSet2.getColumnIndex("ini_max"));
                    int itemId = resultSet2.getInt(resultSet2.getColumnIndex("item_id"));
                    String itemName = resultSet2.getString(resultSet2.getColumnIndex("item_name"));
                    String imageName = resultSet2.getString(resultSet2.getColumnIndex("image"));
                    imageName = imageName.substring(0, imageName.indexOf("."));
                    float itemAmount = resultSet2.getFloat(resultSet2.getColumnIndex("employee_amount"));
                    int dropCount = Integer.parseInt(resultSet2.getString(resultSet2.getColumnIndex("drop_count")));

                    if (availableMenuId == 4 && vegNonVeg == 1) {
                        if (itemId == 4 || itemId == 45 || itemId == 46) {
                            resultSet2.moveToNext();
                            i++;
                            continue;
                        }
                    }

                    CanteenMenu CantBean = new CanteenMenu();

                    CantBean.setMenu_tittle(itemName);
                    CantBean.setMenu_amount(itemAmount);
                    CantBean.setMenu_id(String.valueOf(itemId));
                    CantBean.setDrop_count(dropCount);
                    CantBean.setMenu_Image(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));

                    item_List.add(CantBean);

                    resultSet2.moveToNext();
                    i++;
                }

                resultSet2.close();
                myDatabase.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        checkItemsQuantity();
    }

    public void checkItemsQuantity(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("IAL Cafe");
        alertDialogBuilder.setMessage("Checking Item Availability.........");

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
            int i = 0;
            List<CanteenMenu> remove_list = new ArrayList<>();
            int increament_status = 100/item_List.size();
            @Override
            public void run() {
                while(i < item_List.size()) {
                    CanteenMenu canteenMenu = item_List.get(i);
                    if (quantityGot) {
                        quantityGot = false;

                        jsonObject = new JsonObject();
                        jsonObject.addProperty("empCode", empCode);
                        jsonObject.addProperty("itemId", canteenMenu.getMenu_id());

                        PostGetItemQuantity postGetItemQuantity = new PostGetItemQuantity(context);
                        postGetItemQuantity.checkServerAvailability(1);

                        quantityEntry = false;
                    }
                    if(quantityEntry){
                        quantityGot = true;

                        int dropCount = canteenMenu.getDrop_count();
                        if(quantity < dropCount){
                            canteenMenu.setMenu_quantity(dropCount - quantity);
                            canteenMenu.setDrop_count(dropCount - quantity);
                        }else{
                            remove_list.add(canteenMenu);
                        }

                        progressStatus += increament_status;
                        progressBar.setProgress(progressStatus);
                        i++;
                    }
                }

                for(int j = 0; j < remove_list.size(); j++){
                    CanteenMenu canteenMenu = remove_list.get(j);
                    item_List.remove(canteenMenu);
                }

                alertDialog.dismiss();
                handler.post(new Runnable() {
                    public void run() {
                        CustomListAdapter adapter = new CustomListAdapter(context, item_List);
                        listView.setAdapter(adapter);
                    }
                });
            }
        }).start();

    }

    public void popup_show() {
        String result = "Selected Product are :" + "\n";
        CustomListAdapter adapter = new CustomListAdapter(this, item_List);
        ArrayList displaylist = new ArrayList();
        for (CanteenMenu p : adapter.getBox()) {
            if (p.box) {
                displaylist.add(p.menu_id);
                selectedItemList.add(p);
                result += "\n" + p.menu_tittle+" - "+p.menu_quantity;
            }
        }

        if (displaylist.size() < 0) {
            Toast.makeText(getApplicationContext(), "Select Menu Items..!", Toast.LENGTH_SHORT).show();
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(DisplayMenuActivity.this);
            View promptView = layoutInflater.inflate(R.layout.popup, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DisplayMenuActivity.this);
            alertDialogBuilder.setView(promptView);

            TextView text_pop = (TextView) promptView.findViewById(R.id.popup_textView);
            text_pop.setText(result);

            // setup a dialog window
            androidx.appcompat.app.AlertDialog.Builder builder = alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            updateRemoteDatabase.checkServerAvailability(2);
                            //   dialog.cancel();
                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });

            // create an alert dialog
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    public void showResult(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentTime = sdf.format(c.getTime());
        String employeeCode = empCode;

        Iterator i = selectedItemList.iterator();
        while(i.hasNext()) {
            CanteenMenu canteenMenu = (CanteenMenu) i.next();

            int couponNo = 0;
            int itemId = Integer.parseInt(canteenMenu.getMenu_id());
            int guestId = 0;
            int noOfPerson = 1;
            int quantity = canteenMenu.getMenu_quantity();
            Float amount = canteenMenu.getMenu_amount() * canteenMenu.getMenu_quantity();
            String empCode = employeeCode;
            int rfidCard = Integer.parseInt(this.rfidNo);
            String deviceName = this.deviceName;
            String device = this.deviceModel;
            int categoryId = this.categoryId;
            int otpCode = 0;
            String date = this.todayDate;
            String menu = canteenMenu.getMenu_id();
            int shift = 1;
            String transactionDate = this.currentTime;
            String closedTime = this.currentTime;
            String status = "0";
            String flag = "1";
            String createdBy = "Android";
            String createdOn = this.currentTime;
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
            jsonObject.addProperty("date", this.currentTime);
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
            showResult();
        }

        @Override
        public void onPostUpdate(){
            Intent i = new Intent(DisplayMenuActivity.this, ScanActivity.class);
            i.putExtra("deviceName", deviceName);
            startActivity(i);
        }
    }

    private class CheckServer extends UpdateServerDatabase{
        public CheckServer(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(!isServerAvailable){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setTitle("IAL Cafe");
                alertDialogBuilder.setMessage("Connection to Server not Available!");
                alertDialogBuilder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finishAffinity();
                            }
                        });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }else{
                if(isMenuItemCheckAvailable){
                    menu_display();
                }else {
                    popup_show();
                }
            }
        }

        public void onPostUpdate(){}
    }

    private class PostGetItemQuantity extends PostRequest{
        public PostGetItemQuantity(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlCheckInvoiceHeader, jsonObject);
            }else{
                Toast.makeText(getApplicationContext(), "Connection to Network \nnot Available", Toast.LENGTH_SHORT).show();
                quantityEntry = true;
            }
        }

        public void onFinish(JSONArray jsonArray){
            quantity = 0;

            try{
                int j = 0;
                while (j < jsonArray.length()) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(j);
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject.getString("createdOn"));
                    String time = new SimpleDateFormat("HH:mm:ss").format(date);

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                    if(sdf.parse(time).after(sdf.parse(startTime)) && sdf.parse(time).before(sdf.parse(endTime))) {
                        quantity += jsonObject.getInt("quantity");
                    }

                    j++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            quantityEntry = true;
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(DisplayMenuActivity.this, ScanActivity.class);
        i.putExtra("date", date);
        i.putExtra("currentTime",currentTime);
        i.putExtra("rfidNo", rfidNo);
        i.putExtra("deviceName", deviceName);
        i.putExtra("empCode", empCode);
        i.putExtra("empName", empName);
        i.putExtra("vegNonVeg", vegNonVeg);
        i.putExtra("guestPermit",guestPermit);
        i.putExtra("categoryId", categoryId);
        startActivity(i);
    }

}
