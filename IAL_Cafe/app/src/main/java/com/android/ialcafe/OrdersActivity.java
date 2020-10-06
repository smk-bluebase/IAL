package com.android.ialcafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.graphics.Color.*;


public class OrdersActivity extends AppCompatActivity {

    String date;
    String deviceName;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.7);

        ImageView background = findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        Bundle retrieveIntent = getIntent().getExtras();
        date = retrieveIntent.getString("date");
        deviceName = retrieveIntent.getString("deviceName");

        getSupportActionBar().setTitle("Today's History");

        TableLayout tableLayout = findViewById(R.id.tableHistory);

        checkDate();

        final JsonObject orders = getTodaysOrders();

        boolean hasNext = true;
        int i = 0;
        boolean isBlack = false;

        while(hasNext){
            try{
                JsonObject jsonObject = (JsonObject) orders.get(String.valueOf(i));
                createTableRow(tableLayout, jsonObject, isBlack);
                if(isBlack) isBlack = false;
                else isBlack = true;
                i++;
            }catch(Exception e){
                hasNext = false;
            }
        }

        try {
            TextView dateTextView = findViewById(R.id.dateText);
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);
            dateTextView.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void checkDate(){
        try{
            DataBaseHelper myDatabase = new DataBaseHelper(context);
            myDatabase.deleteInvoiceHeaderTable(date);
            myDatabase.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public JsonObject getTodaysOrders(){
        JsonObject todaysOrders = new JsonObject();

        try{
            SQLiteDatabase myDatabase = openOrCreateDatabase("ial.sqlite", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
            Cursor resultSet = myDatabase.rawQuery(String.format("SELECT * FROM INVOICE_HEADER WHERE DATE = \'%s\'", date), null);
            resultSet.moveToFirst();

            int i = 0;
            while(i < resultSet.getCount()){
                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("empCode", resultSet.getString(7));

                int itemId = resultSet.getInt(2);
                Cursor resultSet2 = myDatabase.rawQuery(String.format("SELECT item_name FROM item_master WHERE item_id = %d", itemId), null);
                resultSet2.moveToFirst();

                String itemName = (resultSet2.getCount() > 0)? resultSet2.getString(0) : "";
                jsonObject.addProperty("itemName", itemName);

                resultSet2.close();

                jsonObject.addProperty("quantity", resultSet.getInt(5));
                jsonObject.addProperty("time", resultSet.getString(16));

                todaysOrders.add(String.valueOf(i), jsonObject);
                i++;
                resultSet.moveToNext();
            }

            resultSet.close();
            myDatabase.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return todaysOrders;
    }

    public void createTableRow(TableLayout tableLayout, JsonObject jsonObject, boolean isBlack){
        String empCode = jsonObject.get("empCode").toString();
        String itemName = jsonObject.get("itemName").getAsString();
        String quantity = jsonObject.get("quantity").toString();
        String time = jsonObject.get("time").toString();

        empCode = empCode.substring(1, empCode.length() - 1);
        time = time.substring(11, time.length() - 1);

        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        if(isBlack) tableRow.setBackgroundColor(Color.rgb(220, 220, 220));
        else tableRow.setBackgroundColor(getResources().getColor(R.color.white));
        tableRow.setPadding(20, 20, 20, 20);

        TextView textView1 = new TextView(this);
        textView1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        textView1.setTextColor(BLACK);
        textView1.setText(empCode);

        TextView textView2 = new TextView(this);
        textView2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        textView2.setTextColor(BLACK);
        textView2.setText(itemName);

        TextView textView3 = new TextView(this);
        textView3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        textView3.setTextColor(BLACK);
        textView3.setText(quantity);

        TextView textView4 = new TextView(this);
        textView4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        textView4.setTextColor(BLACK);
        textView4.setText(time);

        tableRow.addView(textView1);
        tableRow.addView(textView2);
        tableRow.addView(textView3);
        tableRow.addView(textView4);

        tableLayout.addView(tableRow);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(OrdersActivity.this, ScanActivity.class);
        i.putExtra("deviceName", deviceName);
        startActivity(i);
    }
}
