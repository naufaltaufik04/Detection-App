package com.example.detectionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CameraActivity2 extends AppCompatActivity {
    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    Dialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        myDialog = new Dialog(this);
        if (ContextCompat.checkSelfPermission(CameraActivity2.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CameraActivity2.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },100);

        }
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);

        // Open Camera
        camera = Camera.open();

        showCamera = new ShowCamera(this,camera);
        frameLayout.addView(showCamera);

    }
    public void ShowPopup(View v){
        Button cancel;
        Button confirm;
        myDialog.setContentView(R.layout.custompopup);
        cancel = (Button) myDialog.findViewById(R.id.cancel);
        confirm = (Button) myDialog.findViewById(R.id.confirm);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                myDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });


        myDialog.show();
    }
    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        startActivity(switchActivityIntent);
    }
}