package com.zebra.pttmapper.crosscall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start Our Service
        startService(new Intent(this, PTTMapperService.class));

        // End Activity
        finish();
    }

}
