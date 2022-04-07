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
    private Dialog popUp_updateDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        popUp_updateDevice = new Dialog(this);
    }
    public void ShowPopup(View v) {
        TextView txtClose;
        popUp_updateDevice.setContentView(R.layout.activity_updatedevice);
        txtClose = (TextView) popUp_updateDevice.findViewById(R.id.txtClose);
        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUp_updateDevice.dismiss();
            }
        });
        popUp_updateDevice.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUp_updateDevice.show();
    }
}