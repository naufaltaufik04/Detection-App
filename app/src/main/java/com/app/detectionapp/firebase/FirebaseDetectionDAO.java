package com.app.detectionapp.firebase;

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

    /**
     Penambahan data device baru ke firebase
     Parameter:
     - device : data objek device yang berisi nama device
     */
    public Task<Void> addDevice(Device device){
        deviceKey = databaseReference.push().getKey();
        return databaseReference.child(this.deviceKey).setValue(device);
    }

    /**
     Memperbaharui data device tertentu pada firebase
     Parameter:
     - key: nilai key dari device pada firebase
     - device : data objek device yang berisi nama device
     */
    public Task<Void> updateDevice(String key, HashMap<String, Object> device){
        return databaseReference.child(key).updateChildren(device);
    }

    /**
     Penambahan data hasil pendeteksian baru ke firebase
     Parameter:
     - key: nilai key dari device pada firebase
     - detection : data hasil pendeteksian
     */
    public Task<Void> addDetection(String key, Detection detection){
        return databaseReference.child(key).child("detections").push().setValue(detection);
    }

    /**
     Mendapatkan nilai key dari device
     Return:
     nilai key device
     */
    public String getDeviceKey(){
        return this.deviceKey;
    }
}
