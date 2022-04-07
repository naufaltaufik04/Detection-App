package com.example.detectionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class RunningDetection extends AppCompatActivity {
    Camera camera;
    FrameLayout frameLayout;
    ConfigCamera showCamera;
    Dialog popUpStopDetection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runningdetection);
        popUpStopDetection = new Dialog(this);
        if (ContextCompat.checkSelfPermission(RunningDetection.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RunningDetection.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },100);

        }
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);

        // Open Camera
        camera = Camera.open();

        showCamera = new ConfigCamera(this,camera);
        frameLayout.addView(showCamera);

    }
    public void PopupStopDetection(View v){
        Button btn_cancel;
        Button btn_confirm;
        popUpStopDetection.setContentView(R.layout.activity_stopdetection);
        btn_cancel = (Button) popUpStopDetection.findViewById(R.id.btn_cancel);
        btn_confirm = (Button) popUpStopDetection.findViewById(R.id.btn_confirm);
        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                popUpStopDetection.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });
        popUpStopDetection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUpStopDetection.show();
    }
    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, MainMenu.class);
        startActivity(switchActivityIntent);
    }
}