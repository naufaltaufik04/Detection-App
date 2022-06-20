package com.app.detectionapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.detectionapp.entity.Device;
import com.app.detectionapp.firebase.FirebaseDetectionDAO;
import com.app.detectionapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SetupDevice extends AppCompatActivity {
    private static final String SETUP_DEVICE = "SetupDevice";
    private static final String SETUP_SUCCESS = "Setup Sucess";

    SharedPreferences setupPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setupdevice);

        setupPreference = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String setup = setupPreference.getString(SETUP_DEVICE, "");

        if (setup.equals(SETUP_SUCCESS)) {
            finish();
            startActivity(new Intent(SetupDevice.this, MainActivity.class));
        }

        EditText editTextSetupDevice = findViewById(R.id.editTextSetup);

        // Click Listerner Setup Device Name Button
        Button setupButton = findViewById(R.id.btn_save);;
        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceName = editTextSetupDevice.getText().toString();

                if(deviceName.isEmpty()){
                    Toast.makeText(SetupDevice.this, "Device tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                uniqueCheck(deviceName);

            }
        });
    }

    /**
     Pengecekan unik nama device ke firebase
     Parameter:
     - deviceName : data nama device
     */
    public void uniqueCheck(String deviceName){
        Query devices = FirebaseDatabase.getInstance().getReference("Device").orderByChild("name").equalTo(deviceName);
        devices.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(SetupDevice.this, "Device sudah terdaftar", Toast.LENGTH_SHORT).show();
                    return;
                }

                Device device = new Device();
                device.setName(deviceName);

                FirebaseDetectionDAO firebaseDetectionDAO = new FirebaseDetectionDAO();
                firebaseDetectionDAO.addDevice(device);
                device.setKey(firebaseDetectionDAO.getDeviceKey());

                Toast.makeText(SetupDevice.this, "Device berhasil dibuat", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = setupPreference.edit();
                editor.putString(SETUP_DEVICE, SETUP_SUCCESS);
                editor.apply();

                finish();

                Intent mainMenu = new Intent(SetupDevice.this, MainActivity.class);
                mainMenu.putExtra("device", device);
                startActivity(mainMenu);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}