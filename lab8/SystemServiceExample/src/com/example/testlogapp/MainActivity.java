package com.example.testlogapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.app.LedManager;

public class MainActivity extends Activity {

    private static final String TAG = "TestLogApp";
    private LedManager ledManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Link XML UI

        ledManager = (LedManager) getSystemService("led");

        if (ledManager == null) {
            Toast.makeText(this, "LED service not available", Toast.LENGTH_LONG).show();
            Log.e(TAG, "LedManager is null");
            return;
        }

        Button buttonOn = findViewById(R.id.button_led_on);
        Button buttonOff = findViewById(R.id.button_led_off);

        buttonOn.setOnClickListener(v -> {
            ledManager.setLedOn(17);
            Toast.makeText(this, "Wal3aaaa", Toast.LENGTH_SHORT).show();
        });

        buttonOff.setOnClickListener(v -> {
            ledManager.setLedOff(17);
            Toast.makeText(this, "Etfeneeet", Toast.LENGTH_SHORT).show();
        });
    }
}
