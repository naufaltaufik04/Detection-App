package com.example.detectionapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.widget.Button;

public class SetupDevice extends AppCompatActivity {
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setupdevice);

        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String FirstTimeInstall = sharedPreferences.getString("FirstTimeInstall", "");

        if (FirstTimeInstall.equals("Yes")) {
            startActivity(new Intent(SetupDevice.this, MainActivity.class));
        }

        // Click Listerner Setup Device Name Button
        saveButton = findViewById(R.id.btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("FirstTimeInstall", "Yes");
                editor.apply();
                finish();
                startActivity(new Intent(SetupDevice.this, MainActivity.class));
            }
        });

        finish();
    }
}