package com.android.ialcafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class CustomListAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater mlnflater;
    private List<CanteenMenu> menuItems;

    public CustomListAdapter(Context context, List<CanteenMenu> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
        mlnflater= (LayoutInflater) context.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE);
    }

    private   class ViewHolder{
        ImageView thumbinal_imageView;
        TextView menu_Title,menu_id,menu_amount,menu_selectQuanty;
        CheckBox check_box;
        Spinner quantity_Spin;
    }


    @Override
    public int getCount() {
        return menuItems.size() ;
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  position;
    }

   @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;

        if(convertView==null){
            convertView=mlnflater.inflate(R.layout.list_item,null);
            holder=new ViewHolder();

            CanteenMenu p = getProduct(position);

            holder.thumbinal_imageView = convertView.findViewById((R.id.thumbinal));
            holder.menu_Title = convertView.findViewById((R.id.menu_title));
            holder.menu_id = convertView.findViewById(R.id.menu_id);
            holder.menu_amount = convertView.findViewById(R.id.menuAm);
            holder.menu_selectQuanty = convertView.findViewById(R.id.menu_selectQuanty);
            holder.quantity_Spin = convertView.findViewById(R.id.menu_Quantity);

            holder.check_box = convertView.findViewById(R.id.checkId);
            holder.check_box.setOnCheckedChangeListener(myCheckChangList);
            holder.check_box.setTag(position);
            holder.check_box.setChecked(p.box);

            final ViewHolder finalHolder = holder;

            holder.quantity_Spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedValueFromDD = parent.getItemAtPosition(position).toString();
                    CanteenMenu can_it = (CanteenMenu) finalHolder.menu_selectQuanty.getTag();
                    can_it.setMenu_quantity(Integer.parseInt(selectedValueFromDD));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            convertView.setTag(holder);
        } else {
            holder= (ViewHolder) convertView.getTag();
        }


        CanteenMenu cantn_Item = (CanteenMenu) getItem(position);

        holder.menu_Title.setText(cantn_Item.getMenu_tittle());
        System.out.println("getMenu_tittle() : " + cantn_Item.getMenu_tittle());
        holder.menu_id.setText(cantn_Item.getMenu_id());
        System.out.println("this test to drop down ........................" + cantn_Item.getDrop_count());
        holder.menu_amount.setText(String.valueOf(cantn_Item.getMenu_amount()));
        holder.thumbinal_imageView.setImageResource(cantn_Item.getMenu_Image());
        ArrayAdapter dataAdapter;

        List valueList=new ArrayList();
        valueList.clear();
        for(int i=1;i<=cantn_Item.getDrop_count();i++) {
            valueList.add(i);
        }
        dataAdapter = new ArrayAdapter(context,android.R.layout.simple_spinner_item,valueList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.quantity_Spin.setAdapter(dataAdapter);
        final String pass_value = holder.menu_Title.getText().toString();
        final ViewHolder finalHolder = holder;
        holder.menu_selectQuanty.setTag(cantn_Item);
       if(cantn_Item.getMenu_quantity() != 0){
           holder.menu_selectQuanty.setText(String.valueOf(cantn_Item.getMenu_quantity()));
       }
       else {
           holder.menu_selectQuanty.setText("");
       }

        return convertView;

    }

    CanteenMenu getProduct(int position) {
        return ((CanteenMenu) getItem(position));
    }

    ArrayList<CanteenMenu> getBox() {
        ArrayList<CanteenMenu> box = new ArrayList<CanteenMenu>();
        for (CanteenMenu p : menuItems) {
            if (p.box)
                box.add(p);
        }
        return box;
    }

    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
            getProduct((Integer) buttonView.getTag()).box = isChecked;
        }
    };

}