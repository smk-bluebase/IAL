package com.android.ialcafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.JsonObject;


public class OrdersActivity extends AppCompatActivity {

    String date;
    String deviceName;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        Bundle retrieveIntent = getIntent().getExtras();
        date = retrieveIntent.getString("date");
        deviceName = retrieveIntent.getString("deviceName");

        TableLayout tableLayout = findViewById(R.id.tableHistory);

        checkDate();

        final JsonObject orders = getTodaysOrders();

        boolean hasNext = true;
        int i = 0;

        while(hasNext){
            try{
                JsonObject jsonObject = (JsonObject) orders.get(String.valueOf(i));
                createTableRow(tableLayout, jsonObject);
                i++;
            }catch(Exception e){
                hasNext = false;
            }
        }

        TextView dateTextView = findViewById(R.id.dateText);
        dateTextView.setText(date.toString());
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

    public void createTableRow(TableLayout tableLayout, JsonObject jsonObject){
        String empCode = jsonObject.get("empCode").toString();
        String itemName = jsonObject.get("itemName").getAsString();
        String quantity = jsonObject.get("quantity").toString();
        String time = jsonObject.get("time").toString();

        empCode = empCode.substring(1, empCode.length() - 1);
        time = time.substring(11, time.length() - 1);

        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tableRow.setBackgroundColor(getResources().getColor(R.color.light_gray));
        tableRow.setPadding(20, 20, 20, 20);

        TextView textView1 = new TextView(this);
        textView1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        textView1.setTextColor(Color.BLACK);
        textView1.setText(empCode);

        TextView textView2 = new TextView(this);
        textView2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        textView2.setTextColor(Color.BLACK);
        textView2.setText(itemName);

        TextView textView3 = new TextView(this);
        textView3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        textView3.setTextColor(Color.BLACK);
        textView3.setText(quantity);

        TextView textView4 = new TextView(this);
        textView4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        textView4.setTextColor(Color.BLACK);
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
