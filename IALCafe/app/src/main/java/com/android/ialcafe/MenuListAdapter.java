package com.android.ialcafe;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MenuListAdapter extends BaseAdapter {
    private Context context;
    private List<CanteenMenu> items;
    private int categoryId;

    public MenuListAdapter(Context context, List<CanteenMenu> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        CanteenMenu item = (CanteenMenu) getItem(position);

        ImageView thumbNail = convertView.findViewById((R.id.thumbNail));
        TextView itemTitle = convertView.findViewById((R.id.itemTitle));
        Spinner itemQuantitySpinner = convertView.findViewById(R.id.itemQuantitySpinner);
        EditText itemQuantity = convertView.findViewById(R.id.itemQuantity);
        CheckBox itemCheck = convertView.findViewById(R.id.itemCheck);

        thumbNail.setImageResource(item.getMenuImage());
        itemTitle.setText(item.getMenuTitle());

        itemCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setIsChecked(isChecked);
            }
        });

        // Employee User
        if(categoryId != 3) {
            itemQuantity.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            params.width = 130;
            params.height = 100;
            params.setMargins(20, 15, 20, 0);
            itemQuantitySpinner.setLayoutParams(params);

            ArrayAdapter dataAdapter;

            List valueList = new ArrayList();
            valueList.clear();
            for(int i = 1; i <= item.getDropCount(); i++) {
                valueList.add(i);
            }
            dataAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, valueList);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            itemQuantitySpinner.setAdapter(dataAdapter);

            itemQuantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    item.setMenuQuantity(Integer.parseInt(itemQuantitySpinner.getItemAtPosition(position).toString()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    //Do Nothing
                }

            });

        }
        // Contractor User
        else {
            itemQuantitySpinner.setVisibility(View.GONE);
            itemCheck.setEnabled(false);

            CheckBox finalItemCheck = itemCheck;
            itemQuantity.addTextChangedListener(new TextWatcher() {
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
                    try {
                        if (s.toString().length() > 0 && Integer.parseInt(s.toString()) != 0) {
                            item.setMenuQuantity(Integer.parseInt(s.toString()));
                            finalItemCheck.setEnabled(true);
                        } else {
                            finalItemCheck.setChecked(false);
                            finalItemCheck.setEnabled(false);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }

        return convertView;
    }

    public void setCategoryId(int categoryId){
        this.categoryId = categoryId;
    }

}