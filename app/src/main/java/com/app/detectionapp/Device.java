package com.app.detectionapp;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Device implements Parcelable {
    private String name;
    private String key;
    private ArrayList <Detection> detections;

    public Device() {
        this.detections = new ArrayList<>();
        this.detections = null;
    }

    public Device(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public Device(Parcel device) {
        this.name = device.readString();
        this.key = device.readString();
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

    public ArrayList<Detection> getDetections() {
        return detections;
    }

    public void setDetections(ArrayList<Detection> detections) {
        this.detections = detections;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.key);
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
