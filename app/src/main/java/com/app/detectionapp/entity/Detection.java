package com.app.detectionapp.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Detection {
    private String status;
    private String date;

    public Detection(String status){
        this.status = status;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        this.date = simpleDateFormat.format(Calendar.getInstance().getTime()).toString();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
