// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.

package com.example.detectionapp;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 Class PrePostProcessor
 Inisiasi nilai input agar sesuai dengan model, beserta fungsi untuk pengolahan
 ouput yang dihasilkan dari prediksi dengan model yang digunakan
 * */
public class PrePostProcessor {
    static float[] NO_MEAN_RGB = new float[] {0.0f, 0.0f, 0.0f};
    static float[] NO_STD_RGB = new float[] {1.0f, 1.0f, 1.0f};

    static int inputWidth = 640;   // Lebar dari input image
    static int inputHeight = 640;  // Tinggi dari input image

    private static int outputRow = 25200;       // Jumlah output baris default 25200 jika menggunakan input size 640
    private static int outputColumn = 8;        // Jumlah output kolom (x1,y1,lebar,tinggi,confidence skoce,jumlah class(3 class))
    private static float threshold = 0.70f;     // Nilai ambang batas dari confidence score untuk NMS yang akan dipilih
    private static int limit = 15;              // Jumlah batas box dalam satu frame
    static String[] classes;                   // Daftar dari class yang digunakan

    /**
     Algoritma Non Max Suppression yang digunakan YOLOv5
     Menghapus bounding box yang tumpang tindih dengan bounding box lainnya
     yang memiliki skor paling tinggi
     Parameter:
     - boxes: Array dari bouding box beserta dengan skornya
     - limit: Jumlah batas box dalam satu frame
     - threshold: Nilai ambang batas dari skor NMS
     Return:
     Hasil box yang dipilih
     */
    static ArrayList<ResultDetection> nonMaxSuppression(ArrayList<ResultDetection> boxes, int limit, float threshold) {
        // Mengurutkan confidence scores dari yang paling tinggi ke paling rendah
        Collections.sort(boxes,
                new Comparator<ResultDetection>() {
                    @Override
                    public int compare(ResultDetection o1, ResultDetection o2) {
                        return o1.score.compareTo(o2.score);
                    }
                });

        ArrayList<ResultDetection> selected = new ArrayList<>(); // Hasil dari bounding box
        boolean[] active = new boolean[boxes.size()];   // Box yang masih aktif atau belum dihilangkan
        Arrays.fill(active, true);                  // Yang belum dihilangkan ditandai dengan true
        int numActive = active.length;                  // Jumlah box yang belum dihilangkan

        /**
         Dimulai dengan kotak yang memiliki skor tertinggi.
         Hapus kotak yang tersisa yang tumpang tindih lebih dari ambang batas yang diberikan
         Jika ada kotak yang tersisa (kotak ini tidak tumpang tindih dengan yang lain kotak
         sebelumnya), maka ulangi prosedur ini, sampai tidak ada lagi kotak yang tersisa
         atau batas telah tercapai.
         **/
        boolean done = false;
        for (int i=0; i<boxes.size() && !done; i++) { // Selama masih ada box
            if (active[i]) {    // Jika box masih belum dihilangkan
                ResultDetection boxA = boxes.get(i);
                selected.add(boxA);
                if (selected.size() >= limit) break; //Jika batas sudah tercapai

                for (int j=i+1; j<boxes.size(); j++) {
                    if (active[j]) { // Jika box masih belum dihilangkan
                        ResultDetection boxB = boxes.get(j);
                        // Hapus box yang tumpang tindih yang melebihi nilai ambang batas
                        if (IOU(boxA.rect, boxB.rect) > threshold) {
                            active[j] = false;      // Hapus box
                            numActive -= 1;         // Jumlah box kurangi 1
                            if (numActive <= 0) {   // Jika sudah tidak ada box berhenti
                                done = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return selected;
    }

    /**
     Algoritma Intersection-Over-Union yang digunakan dalam NMS
     Mengkalkulasi apakah terjadi tumpang tindih antara dua bounding boxes
     Parameter:
     - a: Bounding box 1
     - b: Bounding box 2
     Return:
     Bagian box yang sudah di seleksi
     */
    static float IOU(Rect a, Rect b) {
        float areaA = (a.right - a.left) * (a.bottom - a.top);  // Menghitung luas box a
        if (areaA <= 0.0) return 0.0f;  // Jika luasnya adalah 0 artinya tidak ada

        float areaB = (b.right - b.left) * (b.bottom - b.top);   // Menghitung luas box b
        if (areaB <= 0.0) return 0.0f;  // Jika luasnya adalah 0 artinya tidak ada

        float intersectionMinX = Math.max(a.left, b.left);      // Box bagian kiri yang paling panjang
        float intersectionMinY = Math.max(a.top, b.top);        // Box bagian atas yang paling panjang
        float intersectionMaxX = Math.min(a.right, b.right);    // Box bagian kanan yang paling pendek
        float intersectionMaxY = Math.min(a.bottom, b.bottom);  // Box bagian bawah yang paling pendek
        float intersectionArea = Math.max(intersectionMaxY - intersectionMinY, 0) *
                Math.max(intersectionMaxX - intersectionMinX, 0); // Luas Intersection
        return intersectionArea / (areaA + areaB - intersectionArea);
    }

    /**
     Merubah dari hasil prediksi kedalam bentuk NMS
     Parameter:
     - outputs      : Hasil prediksi
     - imgScaleX    : Pembagian panjang gambar yang diinputkan dengan panjang gambar input yang dibutuhkan (mInputWidth = 640)
     - imgScaleY    : Pembagian tinggi gambar yang diinputkan dengan tinggi gambar input yang dibutuhkan (mInputHeight = 640)
     - ivScaleX     : Pembagian panjang gambar yang akan ditampilkan dengan panjang gambar yang diinputkan
     - ivScaleY     : Pembagian tinggi gambar yang akan ditampilkan dengan panjang gambar yang diinputkan
     - startX       : Koordinat X awal (0)
     - startY       : Koordinat Y awal (0)
     Return:
     Hasil prediksi yang sudah dilakukan NMS
     */
    static ArrayList<ResultDetection> outputsToNMSPredictions(float[] outputs, float imgScaleX, float imgScaleY, float ivScaleX, float ivScaleY, float startX, float startY) {
        ArrayList<ResultDetection> resultDetections = new ArrayList<>();
        for (int i = 0; i< outputRow; i++) {   // Selama masih ada box (25200)
            if (outputs[i* outputColumn +4] > threshold) {    // Jika skor nya lebih besar dari ambang batas
                float x = outputs[i* outputColumn];    // Isi dengan koordinat x dari box
                float y = outputs[i* outputColumn +1]; // Isi dengan koordinat y dari box
                float w = outputs[i* outputColumn +2]; // Isi dengan lebar box
                float h = outputs[i* outputColumn +3]; // Isi dengan tinggi box

                float left = imgScaleX * (x - w/2);
                float top = imgScaleY * (y - h/2);
                float right = imgScaleX * (x + w/2);
                float bottom = imgScaleY * (y + h/2);

                float max = outputs[i* outputColumn +5]; // Prediksi awal class dengan skor prediksi tertinggi adalah class 1 (Without Mask)
                int cls = 0;
                for (int j = 0; j < outputColumn -5; j++) { // Selama jumlah class yang ada (3 class)
                    if (outputs[i* outputColumn +5+j] > max) { // Jika skor class ini lebih besar dari prediksi awal
                        max = outputs[i* outputColumn +5+j]; // Prediksi diubah menjadi class ini
                        cls = j;
                    }
                }

                Rect rect = new Rect((int)(startX+ivScaleX*left), (int)(startY+top*ivScaleY),
                        (int)(startX+ivScaleX*right), (int)(startY+ivScaleY*bottom));   // Buat box sesuai yang diprediksi yang disesuaikan dengan ukuran gambar yang akan ditampilkan
                ResultDetection resultDetection = new ResultDetection(cls, outputs[i* outputColumn +4], rect);      // Gabungkan hasil prediksi beserta bounding box kedalam format result
                resultDetections.add(resultDetection);
            }
        }
        return nonMaxSuppression(resultDetections, limit, threshold);
    }
}
