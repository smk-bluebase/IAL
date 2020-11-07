package com.android.ialcafe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DisplayMenuActivity extends AppCompatActivity {
    Context context = this;

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
    String date;

    MenuGenerator menuGenerator;
    ListView listView;

    List<CanteenMenu> selectedItemList;

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

        menuGenerator = new MenuGenerator(context);

        CheckServer checkServer = new CheckServer(context);
        checkServer.checkServerAvailability(1);

        TextView empNameTextView = findViewById(R.id.empName);
        empNameTextView.setText(empName);

        TextView empCodeTextView = findViewById(R.id.empCode);
        empCodeTextView.setText(empCode);

        Button submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupShow();
            }
        });

    }

    public void popupShow() {
        String result = "Selected Product are :" + "\n";
        ArrayList displayList = new ArrayList();
        selectedItemList = new ArrayList<>();
        for (CanteenMenu p : menuGenerator.itemList) {
            if (p.getIsChecked()) {
                displayList.add(p.getMenuId());
                selectedItemList.add(p);
                result += "\n" + p.getMenuTitle() + " - " + p.getMenuQuantity();
            }
        }

        if (displayList.size() < 0) {
            Toast.makeText(context, "Select Menu Items..!", Toast.LENGTH_SHORT).show();
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View promptView = layoutInflater.inflate(R.layout.popup, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptView);

            TextView text_pop = (TextView) promptView.findViewById(R.id.popup_textView);
            text_pop.setText(result);

            // setup a dialog window
            androidx.appcompat.app.AlertDialog.Builder builder = alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            InsertInvoiceHeader insertInvoiceHeader = new InsertInvoiceHeader(context);
                            insertInvoiceHeader.setEmpCode(empCode);
                            insertInvoiceHeader.setRFIDNo(rfidNo);
                            insertInvoiceHeader.setDeviceName(deviceName);
                            insertInvoiceHeader.setDeviceModel(deviceModel);
                            insertInvoiceHeader.setCategoryId(categoryId);
                            insertInvoiceHeader.setTodayDate(todayDate);
                            insertInvoiceHeader.setSelectedItemList(selectedItemList);
                            insertInvoiceHeader.checkServerAvailability(1);
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


    private class CheckServer extends PostRequest {
        public CheckServer(Context context) {
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable) {
            if (!isServerAvailable) {
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
            } else {
                menuGenerator.menuDisplay(currentTime, vegNonVeg, categoryId, empCode);
            }
        }

        @Override
        public void onFinish(JSONArray jsonArray) {}
    }

    public void setListViewAdapter(MenuListAdapter menuListAdapter){
        this.listView.setAdapter(menuListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.basic_menu, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(DisplayMenuActivity.this, ScanActivity.class);
                i.putExtra("deviceName", deviceName);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(DisplayMenuActivity.this, ScanActivity.class);
        i.putExtra("deviceName", deviceName);
        startActivity(i);
    }

}
