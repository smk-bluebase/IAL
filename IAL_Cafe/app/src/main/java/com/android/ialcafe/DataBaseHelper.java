package com.android.ialcafe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    Context context;

    public DataBaseHelper(@Nullable Context context) {
        super(context, "ial.sqlite", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating Tables

        // Employee Master
        String createEmployeeMasterTable = "CREATE TABLE employee_master (id INTEGER PRIMARY KEY  NOT NULL ,emp_code VARCHAR NOT NULL ,emp_name VARCHAR NOT NULL ,image VARCHAR NOT NULL ,shift VARCHAR,access VARCHAR NOT NULL ,veg_nonveg INTEGER NOT NULL ,nonveg_count INTEGER NOT NULL ,department VARCHAR NOT NULL ,category_id INTEGER NOT NULL ,designation VARCHAR NOT NULL ,guest_permit INTEGER NOT NULL ,created_by VARCHAR NOT NULL ,created_on DATETIME NOT NULL ,reporting_to VARCHAR NOT NULL ,doj DATE,dob DATE,company VARCHAR,rfid_card VARCHAR NOT NULL ,a_rfid_card INTEGER DEFAULT (null) ,from_date DATETIME DEFAULT (null) , to_date DATETIME, email VARCHAR)";
        db.execSQL(createEmployeeMasterTable);

        // Canteen
        String createCanteenTable = "CREATE TABLE canteen (id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, menu varchar(50) NOT NULL, menu_name varchar(50) NOT NULL, start_time time NOT NULL, end_time time NOT NULL, created_by varchar(50) NOT NULL, created_date datetime NOT NULL)";
        db.execSQL(createCanteenTable);

        // Canteen_Menu
        String createCanteenMenTable = "CREATE TABLE canteen_menu (id INTEGER PRIMARY KEY NOT NULL , item_id INTEGER NOT NULL , canteen_id INTEGER NOT NULL , ini_max INTEGER NOT NULL , created_by VARCHAR NOT NULL , created_on TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP, status INTEGER NOT NULL )";
        db.execSQL(createCanteenMenTable);

        // Item Master
        String createItemMasterTable = "CREATE TABLE item_master (item_id INTEGER PRIMARY KEY NOT NULL, item_name VARCHAR NOT NULL, amount FLOAT NOT NULL, employee_amount FLOAT NOT NULL, company_amount FLOAT NOT NULL, contractor_amount FLOAT NOT NULL DEFAULT (null), image VARCHAR NOT NULL DEFAULT (null), menu INTEGER NOT NULL DEFAULT (null), add_min INTEGER NOT NULL DEFAULT (null), shift INTEGER NOT NULL DEFAULT (null), status INTEGER NOT NULL DEFAULT (null), created_by VARCHAR NOT NULL DEFAULT (null), created_on TIMESTAMP, drop_count INTEGER)";
        db.execSQL(createItemMasterTable);

        // Invoice Header
        String createInvoiceHeaderTable = "CREATE TABLE invoice_header (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, coupon_no INTEGER NOT NULL, item_id INTEGER NOT NULL, guest_id INTEGER NOT NULL, no_of_person INTEGER NOT NULL, quanty INTEGER NOT NULL, amount INTEGER NOT NULL, emp_code VARCHAR NOT NULL, rfid_card INTEGER NOT NULL, device_name VARCHAR NOT NULL, device VARCHAR NOT NULL, category_id INTEGER NOT NULL, otp_code INTEGER NOT NULL, date DATETIME NOT NULL, menu VARCHAR NOT NULL, shift INTEGER NOT NULL, transaction_date DATETIME NOT NULL, closed_time DATETIME NOT NULL, status VARCHAR NOT NULL, flag VARCHAR NOT NULL, created_by VARCHAR, created_on DATETIME, update_status VARCHAR, company_id INTEGER, flag_id INTEGER, guest_name varchar)";
        db.execSQL(createInvoiceHeaderTable);
    }

    public void deleteCanteenTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("canteen", null, null);
        db.close();
    }

    public void insertCanteenTable(int id, String menu, String menu_name, String start_time, String end_time, String created_by, String created_date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("menu", menu);
        cv.put("menu_name", menu_name);
        cv.put("start_time", start_time);
        cv.put("end_time", end_time);
        cv.put("created_by", created_by);
        cv.put("created_date", created_date);

        db.insert("canteen", null, cv);
        db.close();
    }

    public void deleteCanteenMenuTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("canteen_menu", null, null);
        db.close();
    }

    public void insertCanteenMenuTable(int id, int itemId, int canteenId, int iniMax, String createdBy, String createdOn, int status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("item_id", itemId);
        cv.put("canteen_id", canteenId);
        cv.put("ini_max", iniMax);
        cv.put("created_by", createdBy);
        cv.put("created_on", createdOn);
        cv.put("status", status);

        db.insert("canteen_menu", null, cv);
        db.close();
    }

    public void deleteEmployeeMasterTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("employee_master", null, null);
        db.close();
    }

    public void insertEmployeeMasterTable(int id, String empCode, String empName, String image, String shift, String access, int vegNonVeg, int nonVegCount, String department, String categoryId, String designation, int guestPermit, String createdBy, String createdOn, String reportingTo, String doj, String dob, String company, String rfidCard, int aRfidCard, String fromDate, String toDate, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("emp_code", empCode);
        cv.put("emp_name", empName);
        cv.put("image", image);
        cv.put("shift", shift);
        cv.put("access", access);
        cv.put("veg_nonveg", vegNonVeg);
        cv.put("nonveg_count", nonVegCount);
        cv.put("department", department);
        cv.put("category_id", categoryId);
        cv.put("designation", designation);
        cv.put("guest_permit", guestPermit);
        cv.put("created_by", createdBy);
        cv.put("created_on", createdOn);
        cv.put("reporting_to", reportingTo);
        cv.put("doj", doj);
        cv.put("dob", dob);
        cv.put("company", company);
        cv.put("rfid_card", rfidCard);
        cv.put("a_rfid_card", aRfidCard);
        cv.put("from_date", fromDate);
        cv.put("to_date", toDate);
        cv.put("email", email);

        db.insert("employee_master", null, cv);
        db.close();
    }

    public void deleteItemMasterTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("item_master", null, null);
        db.close();
    }

    public void insertItemMasterTable(int itemId, String itemName, Double amount, Double employeeAmount, Double companyAmount, Double contractorAmount, String image, int menu, int addMin, int shift, int status, String createdBy, String createdOn, int dropCount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("item_id", itemId);
        cv.put("item_name", itemName);
        cv.put("amount", amount);
        cv.put("employee_amount", employeeAmount);
        cv.put("company_amount", companyAmount);
        cv.put("contractor_amount", contractorAmount);
        cv.put("image", image);
        cv.put("menu", menu);
        cv.put("add_min", addMin);
        cv.put("shift", shift);
        cv.put("status", status);
        cv.put("created_by", createdBy);
        cv.put("created_on", createdOn);
        cv.put("drop_count", dropCount);

        db.insert("item_master", null, cv);
        db.close();
    }

    public void deleteInvoiceHeaderTable(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("invoice_header", String.format("date != \'%s\'", date), null);
        db.close();
    }

    public void insertInvoiceHeaderTable(int couponNo, int itemId, int guestId, int noOfPerson, int quantity, Float amount, String empCode, int rfidCard, String deviceName, String device, int categoryId, int otpCode, String date, String menu, int shift, String transactionDate, String closedTime, String status, String flag, String createdBy, String createdOn, String updateStatus, int companyId, int flagId, String guestName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("coupon_no", couponNo);
        cv.put("item_id", itemId);
        cv.put("guest_id", guestId);
        cv.put("no_of_person", noOfPerson);
        cv.put("quanty", quantity);
        cv.put("amount", amount);
        cv.put("emp_code", empCode);
        cv.put("rfid_card", rfidCard);
        cv.put("device_name", deviceName);
        cv.put("device", device);
        cv.put("category_id", categoryId);
        cv.put("otp_code", otpCode);
        cv.put("date", date);
        cv.put("menu", menu);
        cv.put("shift", shift);
        cv.put("transaction_date", transactionDate);
        cv.put("closed_time", closedTime);
        cv.put("status", status);
        cv.put("flag", flag);
        cv.put("created_by", createdBy);
        cv.put("created_on", createdOn);
        cv.put("update_status", updateStatus);
        cv.put("company_id", companyId);
        cv.put("flag_id", flagId);
        cv.put("guest_name", guestName);

        db.insert("invoice_header", null, cv);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // As of now no upgrading!
    }
}
