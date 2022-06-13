package com.app.detectionapp.entity;

import com.app.detectionapp.entity.Detection;

import java.util.ArrayList;

public class DetectionResults {
    public static ArrayList<Detection> detections;

    public DetectionResults(){
        this.detections = new ArrayList<>();
    }
}
