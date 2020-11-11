package com.android.ialcafe;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

import static android.content.Context.MODE_ENABLE_WRITE_AHEAD_LOGGING;

public class MenuGenerator {
    Context context;
    public static List<CanteenMenu> itemList;

    boolean quantityGot = true;
    Handler handler = new Handler();
    boolean quantityEntry = false;

    int progressStatus = 0;

    JsonObject jsonObject;
    String urlCheckInvoiceHeader = CommonUtils.IP + "/ialcafe/check_invoice_header.php";

    String startTime;
    String endTime;
    int quantity;

    public MenuGenerator(Context context){
        this.context = context;
    }

    public void menuDisplay(String currentTime, int vegNonVeg, int categoryId, String empCode) {
        itemList = new ArrayList<>();
        quantity = 0;

        try{
            SQLiteDatabase myDatabase = context.openOrCreateDatabase("ial.sqlite", MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
            Cursor resultSet1 = myDatabase.rawQuery(String.format("SELECT MENU, START_TIME, END_TIME FROM CANTEEN WHERE \'%s\' >= start_time AND \'%s\' <= end_time", currentTime, currentTime), null);
            resultSet1.moveToFirst();

            if(resultSet1.getCount() > 0) {
                int availableMenuId = resultSet1.getInt(0);
                startTime = resultSet1.getString(1);
                endTime = resultSet1.getString(2);

                resultSet1.close();
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

                    CantBean.setMenuTitle(itemName);
                    CantBean.setMenuAmount(itemAmount);
                    CantBean.setMenuId(String.valueOf(itemId));
                    CantBean.setDropCount(dropCount);
                    CantBean.setMenuImage(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));

                    itemList.add(CantBean);

                    resultSet2.moveToNext();
                    i++;
                }

                resultSet2.close();
                myDatabase.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        if(itemList.size() > 0) {
            if (categoryId == 3) {
                MenuListAdapter menuListAdapter = new MenuListAdapter(context, itemList);
                menuListAdapter.setCategoryId(categoryId);
                ((DisplayMenuActivity) context).setListViewAdapter(menuListAdapter);
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setTitle("IAL Cafe");
                alertDialogBuilder.setMessage("Checking Item Availability...");

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    int increament_status = 100 / itemList.size();

                    @Override
                    public void run() {
                        while (i < itemList.size()) {
                            CanteenMenu canteenMenu = itemList.get(i);
                            if (quantityGot) {
                                quantityGot = false;

                                jsonObject = new JsonObject();
                                jsonObject.addProperty("empCode", empCode);
                                jsonObject.addProperty("itemId", canteenMenu.getMenuId());

                                PostGetItemQuantity postGetItemQuantity = new PostGetItemQuantity(context);
                                postGetItemQuantity.checkServerAvailability(1);

                                quantityEntry = false;
                            }
                            if (quantityEntry) {
                                quantityGot = true;

                                int dropCount = canteenMenu.getDropCount();
                                if (quantity < dropCount) {
                                    System.out.println("Drop Count : " + (dropCount - quantity));
                                    canteenMenu.setMenuQuantity(dropCount - quantity);
                                    canteenMenu.setDropCount(dropCount - quantity);
                                } else {
                                    remove_list.add(canteenMenu);
                                }

                                progressStatus += increament_status;
                                progressBar.setProgress(progressStatus);
                                i++;
                            }
                        }

                        for (int j = 0; j < remove_list.size(); j++) {
                            CanteenMenu canteenMenu = remove_list.get(j);
                            itemList.remove(canteenMenu);
                        }

                        alertDialog.dismiss();
                        handler.post(new Runnable() {
                            public void run() {
                                MenuListAdapter menuListAdapter = new MenuListAdapter(context, itemList);
                                menuListAdapter.setCategoryId(categoryId);
                                ((DisplayMenuActivity) context).setListViewAdapter(menuListAdapter);
                            }
                        });
                    }
                }).start();
            }
        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setTitle("IAL Cafe");
            alertDialogBuilder.setMessage("No items available at this time!");
            alertDialogBuilder.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            ((DisplayMenuActivity) context).onBackPressed();
                        }
                    });

            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private class PostGetItemQuantity extends PostRequest{
        public PostGetItemQuantity(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlCheckInvoiceHeader, jsonObject);
            }else{
                Toast.makeText(context, "Connection to Network \nnot Available", Toast.LENGTH_SHORT).show();
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

}
