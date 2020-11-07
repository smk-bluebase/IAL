package com.android.ialcafe;

public class CanteenMenu {
    private String menuTitle;
    private String menuId;
    private int menuQuantity;
    private int menuImage;
    private float menuAmount;
    private int dropCount;
    private boolean isChecked;

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public int getDropCount(){
        return dropCount;
    }

    public void setDropCount(int dropCount){
        this.dropCount = dropCount;
    }

    public int getMenuImage() {
        return menuImage;
    }

    public void setMenuImage(int menuImage) {
        this.menuImage = menuImage;
    }

    public int getMenuQuantity() {
        return menuQuantity;
    }

    public void setMenuQuantity(int menuQuantity) {
        this.menuQuantity = menuQuantity;
    }

    public float getMenuAmount() {
        return menuAmount;
    }

    public void setMenuAmount(float menuAmount) {
        this.menuAmount = menuAmount;
    }

    public boolean getIsChecked(){
        return isChecked;
    }

    public void setIsChecked(boolean isChecked){
        this.isChecked = isChecked;
    }

}
