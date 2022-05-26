package com.app.detectionapp;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.camera.core.ImageProxy;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 Class ObjectDetectionActivity
 Melakukan berbagai proses pada gambar yang diperoleh dari CameraX yang hasilnya berupa hasil prediksi
 */
public class FaceMaskDetection extends AbstractCamera<FaceMaskDetection.AnalysisResult> {
    private Module model = null;        // Asset File Model
    private ResultView resultView;      // Tampilan hasil

    // Menampung hasil prediksi dalam bentuk ArrayList
    static class AnalysisResult {
        private final ArrayList<ResultDetection> resultDetections;

        public AnalysisResult(ArrayList<ResultDetection> resultDetections) {
            this.resultDetections = resultDetections;
        }
    }

    // Inisiasi layout object detection untuk diterapkan ke dalam suatu contentview
    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_object_detection;
    }

    @Override
    protected void stopDetection() {
        // Click Listerner Stop Detection Button
        ImageButton stopDetection = findViewById(R.id.bt_open);
        stopDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmation();
            }
        });

        // Click Listerner Stop Detection Button menggunakan back button pada navigation bar
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                showConfirmation();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public void showConfirmation(){
        Dialog stopConfirmation = new Dialog(this);
        stopConfirmation.setContentView(R.layout.activity_stopdetection);

        stopConfirmation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        stopConfirmation.show();

        // Click Listerner Cancle Stop Detection Button
        Button cancelButton = (Button) stopConfirmation.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                stopConfirmation.dismiss();
            }
        });

        // Click Listerner Confirm Stop Detection
        Button btn_confirm = (Button) stopConfirmation.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Inisiasi layout object detection texture view yang digunakan saat setup Camera di class AbstractCameraXActivity
    @Override
    protected TextureView getCameraPreviewTextureView() {
        resultView = findViewById(R.id.resultView);
        return ((ViewStub) findViewById(R.id.object_detection_texture_view_stub))
                .inflate() //  membaca layout xml
                .findViewById(R.id.object_detection_texture_view);
    }

    /** Menerapkan tampilan hasil dengan hasil deteksi yang diperoleh
     Parameter:
     - result : objek class AnalysisResult yang menampung hasil prediksi
     */
    @Override
    protected void applyToUiAnalyzeImageResult(AnalysisResult result) {
        resultView.setResults(result.resultDetections); // Menerapkan hasil deteksi pada tampilan hasil
        resultView.invalidate(); // Menggambar ulang jika terjadi perubahan
    }

    /**
     CameraX memiliki default format image output yaitu YUV_420_888
     Mengkonversi format image dari YUV_420_888 ke NV21, lalu dari NV21 ke JPEG
     Parameter:
     - Image : Gambar yang akan dilakukan proses deteksi
     Return :
     Hasil decoded bitmap
     */
    private Bitmap imgToBitmap(Image image) {
        // Konversi format YUV_420_888 -> NV21
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        // Konversi format NV21 -> JPEG
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }


    /**
     Memproses gambar yang diinputkan kedalam bentuk ArrayList
     Parameter:
     - image : gambar yang diinputkan
     - rotationDegrees: derajat rotasi gambar
     Return:
     Hasil prediksi yang sudah diolah
     */
    @Override
    @WorkerThread
    @Nullable
    protected AnalysisResult analyzeImage(ImageProxy image, int rotationDegrees) {
        // Sebuah exception ketika melakukan loading file model
        try {
            if (model == null) {
                model = LiteModuleLoader.load(MainActivity.assetFilePath(getApplicationContext(), "face-mask.torchscript.ptl"));
            }
        } catch (IOException e) {
            Log.e("Object Detection", "Error reading assets", e);
            return null;
        }
        Bitmap bitmap = imgToBitmap(image.getImage()); //Membuat Bitmap hasil konversi dari gambar yang diinputkan
        Matrix matrix = new Matrix();
        matrix.postRotate(90.0f); // Mengatur rotasi bitmap
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);                        // Membuat bitmap dengan ukuran panjang lebar sesuai bitmapnya
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, PrePostProcessor.inputWidth, PrePostProcessor.inputHeight, true);  // Meresize bitmap dengan ukuran panjang lebar sesuai input yang dibutuhkan (mInputWidth = 640, mInputHeight= 640)

        // menyiapkan input tensor
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, PrePostProcessor.NO_MEAN_RGB, PrePostProcessor.NO_STD_RGB);

        // menjalankan model
        IValue[] outputTuple = model.forward(IValue.from(inputTensor)).toTuple();
        final Tensor outputTensor = outputTuple[0].toTensor();

        // Memproses hasil
        final float[] outputs = outputTensor.getDataAsFloatArray();

        float imgScaleX = (float)bitmap.getWidth() / PrePostProcessor.inputWidth;      // Pembagian panjang gambar yang diinputkan dengan panjang gambar input yang dibutuhkan (mInputWidth = 640)
        float imgScaleY = (float)bitmap.getHeight() / PrePostProcessor.inputHeight;    // Pembagian tinggi gambar yang diinputkan dengan tinggi gambar input yang dibutuhkan (mInputHeight = 640)
        float ivScaleX = (float) resultView.getWidth() / bitmap.getWidth();             // Pembagian panjang gambar yang akan ditampilkan dengan panjang gambar yang diinputkan
        float ivScaleY = (float) resultView.getHeight() / bitmap.getHeight();           // Pembagian tinggi gambar yang akan ditampilkan dengan panjang gambar yang diinputkan

        final ArrayList<ResultDetection> resultDetections = PrePostProcessor.outputsToNMSPredictions(outputs, imgScaleX, imgScaleY, ivScaleX, ivScaleY, 0, 0); // Menyimpan hasil prediksi yang sudah dilakukan NMS ke dalam ArrayList results
        return new AnalysisResult(resultDetections);
    }
}
