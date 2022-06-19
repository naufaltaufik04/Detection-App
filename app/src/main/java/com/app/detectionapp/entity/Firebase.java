package com.app.detectionapp.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.app.detectionapp.detection.FirebaseDetectionDAO;
import com.app.detectionapp.result.ResultDetection;
import com.app.detectionapp.result.ResultProcessor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Firebase extends AsyncTask<ArrayList<ResultDetection>, Void, String>{
    private FirebaseDetectionDAO firebaseDetectionDAO;
    private Device device;

    public Firebase(Context context){
        this.firebaseDetectionDAO = new FirebaseDetectionDAO();
        SharedPreferences devicePreferences = context.getSharedPreferences("DEVICE", 0);
        this.device = new Device(devicePreferences.getString("name", ""),
                devicePreferences.getString("key", ""), devicePreferences.getString("location", "") );
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(ArrayList<ResultDetection>... resultDetections) {
        saveResult(resultDetections[0]);
        if(DetectionResults.detections.size() >= 10){
            sendResult();
        }

        return "Success";
    }
    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
    }

    public void saveResult(ArrayList<ResultDetection> resultDetections){
        for(ResultDetection result: resultDetections){
            Detection detection = new Detection(ResultProcessor.classes[result.status]);
            DetectionResults.detections.add(detection);
        }
    }

    public void sendResult(){
        Query detection = FirebaseDatabase.getInstance().getReference("Device").orderByChild("name").equalTo(this.device.getName());
        detection.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(Detection detection: DetectionResults.detections){
                    firebaseDetectionDAO.addDetection(device.getKey(), detection);
                }

                DetectionResults.detections.clear();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
