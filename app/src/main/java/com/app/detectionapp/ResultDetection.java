package com.app.detectionapp;

import android.graphics.Rect;

import java.util.ArrayList;

/**
 Class Result
 Entity class untuk hasil prediksi yang sudah diolah
 * */
class ResultDetection {
    int classIndex;     // Index dari class (rentang 0 - 2)
    Float score;        // Score dari class
    Rect rect;          // Bounding Box dari wajah
    ArrayList<Float> wh;          // ArrayList BoundingBox dari wajah

    public ResultDetection(int cls, Float output, Rect rect, ArrayList<Float> wh) {
        this.classIndex = cls;
        this.score = output;
        this.rect = rect;
        this.wh = wh;
    }
}
