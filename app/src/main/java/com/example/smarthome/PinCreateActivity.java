package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class PinCreateActivity extends AppCompatActivity {
    private View[] dots = new View[4];
    private StringBuilder pin = new StringBuilder();
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_create);

        prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
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
    }

    private void addDigit(String digit) {
        if (pin.length() < 4) {
            pin.append(digit);
            updateDots();
            if (pin.length() == 4) {
                prefs.edit().putString("pin_code", pin.toString()).apply();
                startActivity(new Intent(PinCreateActivity.this, AddAddressActivity.class));
                finish();
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