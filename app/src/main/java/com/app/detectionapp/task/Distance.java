package com.app.detectionapp.task;

/**
 Class Distance
 Melakukan proses perhitungan jarak
 */
public class Distance {
    public static final double ACCEPTED_DISTANCE = 250; //cm

    /**
     Estimasi jarak berdasarkan ukuran panjang dan lebar box hasil pendeteksian
     Parameter:
     - width : ukuran panjang bounding box hasil pendeteksian
     - height : ukuran lebar bounding box hasil pendeteksian
     Return :
     Nilai perkiraan jarak
     */
    public double distanceEstimation (float width, float height) {
        return (2 * 3.14 * 180) / (width + height * 360) * 1000 + 3;
    }

    /**
     Konversi nilai inchi ke centimeter
     Parameter:
     - value : nilai dalam inchi
     Return :
     Nilai dalam centimeter
     */
    public double inchToCm(double value){
        return value/2.54;
    }
}
