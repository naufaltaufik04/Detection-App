package com.app.detectionapp.entity;

public class Distance {

    public double distanceEstimation (float width, float height) {
        return (2 * 3.14 * 180) / (width + height * 360) * 1000 + 3;
    }
}
