package com.example.testlogapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.LedManager; 

public class MainActivity extends Activity {

    private static final String TAG = "TestLogApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LedManager ledManager = (LedManager) getSystemService("led");

        if (ledManager == null) {
            Toast.makeText(this, "Log service not available", Toast.LENGTH_LONG).show();
            Log.e(TAG, "LogManager is null");
            return;
        }

        ledManager.setLedOn(17);


        Toast.makeText(this, "Called log service, check logcat", Toast.LENGTH_LONG).show();
        finish();
    }
}

