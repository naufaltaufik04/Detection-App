package com.app.detectionapp;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FirebaseDetectionDAO implements DetectionDAO{
    private DatabaseReference databaseReference;
    private String key;

    public FirebaseDetectionDAO(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Device.class.getSimpleName());

    }
    public Task<Void> addDevice(Device device){
        key = databaseReference.push().getKey();
        System.out.println("HAHAHAHA");
        return databaseReference.child(this.key).setValue(device);
    }

    public Task<Void> updateDevice(String key, HashMap<String, Object> hashMap){
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> addDetection(Detection detection){
        return databaseReference.child(this.key).child("detections").push().setValue(detection);
    }

    public String getKey(){
        return this.key;
    }
}
