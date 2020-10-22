package com.android.ialcafe;

import android.widget.ArrayAdapter;

public class CanteenMenu {

    String menu_tittle;
    String menu_id;
    int menu_quantity;
    int menu_Image;
    boolean  box;
    float menu_amount;
    int drop_count;
    String spinnerValue;

   ArrayAdapter menu_adapter;

    public ArrayAdapter getMenu_adapter() {
        return menu_adapter;
    }

    public void setMenu_adapter(ArrayAdapter menu_adapter) {
        this.menu_adapter = menu_adapter;
    }

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public String getMenu_tittle() {
        return menu_tittle;
    }

    public void setMenu_tittle(String menu_tittle) {
        this.menu_tittle = menu_tittle;
    }

    public void setDrop_count(int menu_drop){
        this.drop_count = menu_drop;
    }

    public int getDrop_count(){
        return drop_count;
    }

    public int getMenu_Image() {
        return menu_Image;
    }

    public void setMenu_Image(int menu_Image) {
        this.menu_Image = menu_Image;
    }

    public int getMenu_quantity() {
        return menu_quantity;
    }

    public void setMenu_quantity(int menu_quantity) {
        this.menu_quantity = menu_quantity;
    }

    public float getMenu_amount() {
        return menu_amount;
    }

    public void setMenu_amount(float menu_amount) {
        this.menu_amount = menu_amount;

    }

    public String getSpinnerValue() {
        return spinnerValue;
    }

    public void setSpinnerValue(String spinnerValue) {
        this.spinnerValue = spinnerValue;
    }
}
