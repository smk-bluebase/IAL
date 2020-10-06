package com.android.ialcafe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScanActivity extends AppCompatActivity {
    EditText rfid;

    Context context = this;
    String dateStr = "";
    String deviceName = "";

    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    String currentTime = sdf.format(c.getTime());
    Date date = new Date();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.7);

        ImageView background = findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        dateStr = new SimpleDateFormat("yyyy-MM-dd").format(Date.parse(date.toString()));

        rfid = findViewById(R.id.rfidNo);

        Bundle retrieveIntent = getIntent().getExtras();
        deviceName = retrieveIntent.getString("deviceName");

        rfid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do Nothing!
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do Nothing!
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() >= 10) {
                    categorySelection(true);
                }
            }
        });

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categorySelection(false);
            }
        });
       
    }

    public void categorySelection(Boolean isWatched){
        String rfidNo = rfid.getText().toString();
        if (rfidNo.equals("")) {
            Toast.makeText(getApplicationContext(), "Scan Your Card", Toast.LENGTH_SHORT).show();
        } else{
            String empName = "";
            String empCode = "";
            String empCategoryId = "";
            String vegNonVeg = "";
            String guestPermit = "";
            String categoryId = "";

            boolean validCode = false;

            try{
                SQLiteDatabase myDataBase = openOrCreateDatabase("ial.sqlite", MODE_ENABLE_WRITE_AHEAD_LOGGING,null);
                Cursor resultSet = myDataBase.rawQuery(String.format("SELECT * FROM employee_master WHERE rfid_card = \'%s\'", rfidNo), null);
                resultSet.moveToFirst();

                if(resultSet.getCount() >= 1) {
                    validCode = true;
                    empName = resultSet.getString(2);
                    empCode = resultSet.getString(1);
                    empCategoryId = resultSet.getString(9);
                    vegNonVeg = resultSet.getString(6);
                    guestPermit = resultSet.getString(11);
                    categoryId = resultSet.getString(9);
                }

                resultSet.close();
                myDataBase.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            if(!validCode){
                if(!isWatched) {
                    Toast.makeText(getApplicationContext(), "Enter Valid code", Toast.LENGTH_SHORT).show();
                    rfid.setText("");
                }
            }else{
                Intent i = new Intent(ScanActivity.this, DisplayMenuActivity.class);
                i.putExtra("date", date.toString());
                i.putExtra("currentTime", currentTime);
                i.putExtra("rfidNo", rfidNo);
                i.putExtra("deviceName", deviceName);
                i.putExtra("empName", empName);
                i.putExtra("empCode", empCode);
                i.putExtra("empCategoryId", empCategoryId);
                i.putExtra("vegNonVeg", vegNonVeg);
                i.putExtra("guestPermit", guestPermit);
                i.putExtra("categoryId", categoryId);
                startActivity(i);
                rfid.setText("");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_listview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_update:
                UpdateLocal updateLocal = new UpdateLocal(context);
                updateLocal.checkServerAvailability(2);
                return true;

            case R.id.menu_orders:
                Intent i  = new Intent(ScanActivity.this, OrdersActivity.class);
                i.putExtra("date", dateStr);
                i.putExtra("deviceName", deviceName);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class UpdateLocal extends UpdateLocalDatabase {

        public UpdateLocal(Context context) {
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(!isServerAvailable) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setTitle("IAL Cafe");
                alertDialogBuilder.setMessage("Connection to Server not Available!");
                alertDialogBuilder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setTitle("IAL Cafe");
                alertDialogBuilder.setMessage("Do you want to update database?");
                alertDialogBuilder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                UpdateLocal updateLocal = new UpdateLocal(context);
                                updateLocal.updateAll();
                                dialog.cancel();
                            }
                        });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }

        public void onPostUpdate() {

        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ScanActivity.this, LoginActivity.class);
        startActivity(i);
    }
}