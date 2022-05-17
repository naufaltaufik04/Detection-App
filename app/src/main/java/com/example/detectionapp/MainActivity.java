// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.

package com.example.detectionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Runnable {
    private Module model = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Storage Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        setContentView(R.layout.activity_main); // menampilkan halaman ui activity main

        // Load model dan labels
        try {
            // Model
            model = LiteModuleLoader.load(MainActivity.assetFilePath(getApplicationContext(), "face-mask.torchscript.ptl")); //Memuat modul TorchScript serial dari jalur yang ditentukan pada disk untuk dijalankan pada perangkat yang ditentukan

            // Label
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("face-mask.txt"))); //untuk membaca teks dari aliran input berbasis karakter. Ini dapat digunakan untuk membaca data baris demi baris dengan metode readLine().
            String line;
            List<String> classes = new ArrayList<>();
            while ((line = br.readLine()) != null) { // Selama baris dalam file tidak kosong
                classes.add(line); // Tambahkan baris ke list class
            }
            PrePostProcessor.classes = new String[classes.size()]; // Definisikan jumlah array sesuai jumlah class
            classes.toArray(PrePostProcessor.classes); //mengembalikan array yang berisi semua elemen dalam ArrayList dalam urutan yang benar.
        } catch (IOException e) {
            Log.e("Object Detection", "Error reading assets", e);
            finish();
        }

        // Click Listerner Update Device Name Button
        final ImageButton updateDevice = findViewById(R.id.updateDevice);
        updateDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdate();
            }
        });

        // Click Listener Start Detection Button
        final ImageButton startDetection = findViewById(R.id.startDetection);
        startDetection.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Perpindahan UI menggunakan intent activity
                final Intent intent = new Intent(MainActivity.this, FaceMaskDetection.class);
                startActivity(intent);
            }
        });
    }

    /**
     Inisiasi dan membuat custom popup update device name beserta dengan fungsinya
     */
    public void showUpdate() {
        Dialog popupUpdateDevice = new Dialog(this);
        popupUpdateDevice.setContentView(R.layout.activity_updatedevice);

        popupUpdateDevice.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupUpdateDevice.show();

        // Click Listener close popup
        ImageButton close = popupUpdateDevice.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupUpdateDevice.dismiss();
            }
        });

        // Click Listener update device name
        Button updateDevice = popupUpdateDevice.findViewById(R.id.btn_save);
        updateDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupUpdateDevice.dismiss();
            }
        });

    }

    /**
     Periksa assets pada penyimpanan android dan mengembalikan absolute path nya
     Parameter:
     - context:
     - assetName: Nama asset
     Return:
     Absolute path dari asset
     */
    public static String assetFilePath(Context context, String assetName) throws IOException { //informasi path aset berbasis file. returns file lokal dengan nama
        File file = new File(context.getFilesDir(), assetName); //Lokasi penyimpanan internal menggunakan parameter nama file Anda
        if (file.exists() && file.length() > 0) { // jika file ada dan panjang file lebih dari nol
            return file.getAbsolutePath();  //maka mengembalikan nama path abosolut dari objek file yang diberikan, jika absolut maka hanya mengembalikan path dari objek saat ini
        }

        try (InputStream is = context.getAssets().open(assetName)) { //get file asset, lalu buka file aset
            try (OutputStream os = new FileOutputStream(file)) { //outputstream untuk menulis data/aliran byte mentah ke file atau menyimpan data ke file. FileOutputStream adalah subkelas dari OutputStream.
                byte[] buffer = new byte[4 * 1024]; // membuat array byte untuk menyalin data
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read); //menulis data yg benar benar terbaca
                }
                os.flush(); //flush konten buffer ke output stream
            }
            return file.getAbsolutePath(); //mengembalikan nama path abosolut dari objek file yang diberikan, jika absolut maka hanya mengembalikan path dari objek saat ini
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void run() {

    }
}
