package com.app.detectionapp.detection;

import com.app.detectionapp.entity.Detection;
import com.app.detectionapp.entity.Device;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class FirebaseDetectionDAO implements DetectionDAO{
    private DatabaseReference databaseReference;
    private String deviceKey = null;

    public FirebaseDetectionDAO(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Device.class.getSimpleName());

    }
    public Task<Void> addDevice(Device device){
        deviceKey = databaseReference.push().getKey();
        return databaseReference.child(this.deviceKey).setValue(device);
    }

    public Task<Void> updateDevice(String key, HashMap<String, Object> hashMap){
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> addDetection(String key, Detection detection){
        return databaseReference.child(key).child("detections").push().setValue(detection);
    }

    public String getDeviceKey(){
        return this.deviceKey;
    }
}
