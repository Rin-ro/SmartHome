package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    // SplashActivity.java (исправленный)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        SharedPreferences prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        String pin = prefs.getString("pin_code", null);
        boolean isLoggedIn = prefs.contains("email");
        new Handler().postDelayed(() -> {
            if (pin != null && isLoggedIn) {
                startActivity(new Intent(SplashActivity.this, PinLoginActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, AuthActivity.class));
            }
            finish();
        }, 2000);
    }
}