package com.app.detectionapp.firebase;

import com.app.detectionapp.entity.Detection;
import com.app.detectionapp.entity.Device;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

public interface DetectionDAO {
    public Task<Void> addDevice(Device device);
    public Task<Void> updateDevice(String key, HashMap<String, Object> hashMap);
    public Task<Void> addDetection(String key, Detection detection);
}
