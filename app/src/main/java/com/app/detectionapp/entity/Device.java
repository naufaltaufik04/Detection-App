package com.app.detectionapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Device implements Parcelable {
    private String name;
    private String location;
    private String key;
    private ArrayList <Detection> detections;
    private String detectionKey;

    public Device() {
        this.detections = new ArrayList<>();
        this.key = null;
        this.location = null;
        this.detectionKey = null;
    }

    public Device(String name, String key, String location) {
        this.name = name;
        this.key = key;
        this.location = location;
        this.detectionKey = detectionKey;
    }

    public Device(Parcel device) {
        this.name = device.readString();
        this.key = device.readString();
        this.location = device.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public ArrayList<Detection> getDetections() {
        return detections;
    }

    public void setDetections(ArrayList<Detection> detections) {
        this.detections = detections;
    }

    public String getDetectionKey() {
        return detectionKey;
    }

    public void setDetectionKey(String detectionKey) {
        this.detectionKey = detectionKey;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.key);
        parcel.writeString(this.location);
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
}
