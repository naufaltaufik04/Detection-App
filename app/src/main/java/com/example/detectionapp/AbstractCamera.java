// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.

package com.example.detectionapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Rational;
import android.util.Size;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;

public abstract class AbstractCamera<R> extends BaseModule {
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 200;  // Success camera permission code
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA}; // Permission camera
    private static final double RATIO_4_3_VALUE = 4.0 / 3.0;
    private static final double RATIO_16_9_VALUE = 16.0 / 9.0;

    private long mLastAnalysisResultTime;

    protected abstract int getContentViewLayoutId();    // Abstarct fungsi get id layout face detection
    protected abstract void stopDetection();            // Abstract fungsi berhenti pendeteksian

    protected abstract TextureView getCameraPreviewTextureView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());

        // Menyembunyikan status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startBackgroundThread(); // Memulai thread asynchronus

        // Cek permission camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    REQUEST_CODE_CAMERA_PERMISSION);
        } else {
            setupCameraX();     // Inisiasi / memulai camera
            stopDetection();    // Inisiasi fungsi untuk pemberhentian pendeteksian
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(
                        this,
                        "You can't use object detection example without granting CAMERA permission",
                        Toast.LENGTH_LONG)
                        .show();
                finish();
            } else {
                setupCameraX();
            }
        }
    }

    /**
     Mendapatkan nilai aspect ratio sesuai dengan resolusi layar yang digunakan device
     Return:
     Nilai ratio dengan Tipe Rational
     */
    private Rational getScreenRatio(){
        // Mendapatkan resolusi layar yang digunakan
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        double previewRatio = Double.valueOf(Math.max(width, height)) / Math.min(width, height);
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            return new Rational(3,4);
        }else{
            return new Rational(9,16);
        }
    }

    /**
     Pengaturan cameraX untuk mendefinisikan ukuran, ratio, rotation dari layar ataupun dari output
     yang diambil saat melakukan realtime preview
     */
    private void setupCameraX() {
        final TextureView textureView = getCameraPreviewTextureView();

        //Inisiasi preview untuk realtime camera
        final PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setTargetAspectRatio(getScreenRatio())
                .build();
        final Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(output -> textureView.setSurfaceTexture(output.getSurfaceTexture()));

        //Inisiasi untuk output frame yang diambil dari hasil realtime camera
        final ImageAnalysisConfig imageAnalysisConfig =
                new ImageAnalysisConfig.Builder()
                        .setTargetResolution(new Size(480, 640))
                        .setTargetAspectRatio(getScreenRatio())
                        .setCallbackHandler(mBackgroundHandler)
                        .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                        .build();
        final ImageAnalysis imageAnalysis = new ImageAnalysis(imageAnalysisConfig);
        imageAnalysis.setAnalyzer((image, rotationDegrees) -> {
            if (SystemClock.elapsedRealtime() - mLastAnalysisResultTime < 500) {
                return;
            }

            final R result = analyzeImage(image, rotationDegrees);
            if (result != null) {
                mLastAnalysisResultTime = SystemClock.elapsedRealtime();
                runOnUiThread(() -> applyToUiAnalyzeImageResult(result));
            }
        });

        CameraX.bindToLifecycle(this, preview, imageAnalysis);
    }

    @WorkerThread
    @Nullable
    protected abstract R analyzeImage(ImageProxy image, int rotationDegrees);

    @UiThread
    protected abstract void applyToUiAnalyzeImageResult(R result);
}
