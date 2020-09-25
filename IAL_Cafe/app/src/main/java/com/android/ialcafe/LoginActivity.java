package com.android.ialcafe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        context = this;

        inputDeviceName = findViewById(R.id.usernameid);
        inputDeviceName.setText(deviceName);
        inputDeviceName.setKeyListener(null);

        inputDevicePassword = findViewById(R.id.passwordId);

        ((Button)findViewById(R.id.btnLogin)).setOnClickListener(new View.OnClickListener() {
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

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}