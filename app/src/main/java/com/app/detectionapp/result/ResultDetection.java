package com.app.detectionapp.result;

import android.graphics.Rect;

import java.util.Map;

/**
 Class Result
 Entity class untuk hasil prediksi yang sudah diolah
 * */
public class ResultDetection {
    public int status;     // Index dari class (rentang 0 - 2)
    Float score;        // Score dari class
    Rect box;          // Bounding Box dari wajah
    public Map<String, Float> boxOriginal;

    public ResultDetection(int status, Float score, Rect box, Map<String, Float> boxOriginal) {
        this.status = status;
        this.score = score;
        this.box = box;
        this.boxOriginal = boxOriginal;
    }
}
