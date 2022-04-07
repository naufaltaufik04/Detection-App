package com.example.detectionapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {
    private Dialog popupUpdateDevice;
    private ImageButton imgBtnStartDetection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        popupUpdateDevice = new Dialog(this);

        imgBtnStartDetection = (ImageButton) findViewById(R.id.imgBtn_startDetection);
        imgBtnStartDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });
    }
    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, RunningDetection.class);
        startActivity(switchActivityIntent);
    }
    public void PopupUpdate(View v) {
        TextView txtClose;
        popupUpdateDevice.setContentView(R.layout.activity_updatedevice);
        txtClose = (TextView) popupUpdateDevice.findViewById(R.id.txtClose);
        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupUpdateDevice.dismiss();
            }
        });
        popupUpdateDevice.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupUpdateDevice.show();
    }
}