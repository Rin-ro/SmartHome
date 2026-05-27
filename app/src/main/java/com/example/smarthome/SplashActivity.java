package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        boolean hasPin = prefs.contains("pin_code");
        new Handler().postDelayed(() -> {
            if (hasPin) {
                startActivity(new Intent(this, PinLoginActivity.class));
            } else {
                startActivity(new Intent(this, AuthActivity.class));
            }
            finish();
        }, 2000);
    }
}