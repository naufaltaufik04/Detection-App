package com.app.detectionapp;

public class Detection {
    private String date;
    private String status;

    public Detection(String date, String status){
        this.date = date;
        this.status = status;
    }
    public Detection(){

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
