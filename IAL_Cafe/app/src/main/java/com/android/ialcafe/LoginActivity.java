package com.android.ialcafe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText inputDeviceName;
    EditText inputDevicePassword;
    String deviceName = "android4";
    String devicePassword = "";

    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.7);

        ImageView background = findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        context = this;

        inputDeviceName = findViewById(R.id.username);
        inputDeviceName.setText(deviceName);
        inputDeviceName.setKeyListener(null);

        inputDevicePassword = findViewById(R.id.password);

        ((Button)findViewById(R.id.login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devicePassword = inputDevicePassword.getText().toString();
                if (devicePassword.equals(deviceName)) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    inputDevicePassword.setText("");

                    Intent i = new Intent(LoginActivity.this, ScanActivity.class);
                    i.putExtra("deviceName", deviceName);
                    startActivity(i);
                } else {
                    Toast.makeText(LoginActivity.this, "Does not Match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        CheckServer checkServer = new CheckServer(context);
        checkServer.checkServerAvailability(2);
    }

    private class CheckServer extends UpdateServerDatabase{
        public CheckServer(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(!isServerAvailable){
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
            }else{
                Toast.makeText(LoginActivity.this, "Server Connection Available!", Toast.LENGTH_SHORT).show();
            }
        }

        public void onPostUpdate(){}
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}