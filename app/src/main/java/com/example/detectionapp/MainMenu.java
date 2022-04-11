package com.example.detectionapp;

import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {
    private Dialog popupUpdateDevice;
    private ImageButton imgBtnStartDetection;
    private Button btn_upload;
    private static final int PICK_IMAGE = 1;

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

        //Upload
        btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivitiesUpload();
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

    //Upload
    private void switchActivitiesUpload() {
        Intent switchActivityIntent = new Intent(this, UploadImage.class);
        startActivity(switchActivityIntent);
    }


}