package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class PinLoginActivity extends AppCompatActivity {

    private LinearLayout dotsLayout;
    private View[] dots = new View[4];
    private StringBuilder pin = new StringBuilder();
    private SharedPreferences prefs;
    private String savedPin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pin_login);
        prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        savedPin = prefs.getString("pin_code", "0000");

        dots[0] = findViewById(R.id.dot1);
        dots[1] = findViewById(R.id.dot2);
        dots[2] = findViewById(R.id.dot3);
        dots[3] = findViewById(R.id.dot4);

        int[] buttonIds = {R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5,
                R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn0};
        for (int id : buttonIds) {
            MaterialButton btn = findViewById(id);
            btn.setOnClickListener(v -> {
                String digit = ((MaterialButton) v).getText().toString();
                addDigit(digit);
            });
        }

        MaterialButton logoutBtn = findViewById(R.id.loginButton);
        logoutBtn.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        });
    }
    private void addDigit(String digit) {
        if (pin.length() < 4) {
            pin.append(digit);
            updateDots();
            if (pin.length() == 4) {
                if (pin.toString().equals(savedPin)) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Неверный пин-код", Toast.LENGTH_SHORT).show();
                    pin.setLength(0);
                    updateDots();
                }
            }
        }
    }

    private void updateDots() {
        for (int i = 0; i < dots.length; i++) {
            if (i < pin.length()) {
                dots[i].setBackgroundResource(R.drawable.bg_dot_filled);
            } else {
                dots[i].setBackgroundResource(R.drawable.bg_dot_empty);
            }
        }
    }
}