package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddAddressActivity extends AppCompatActivity {
    private EditText editAddress;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        editAddress = findViewById(R.id.editAddress);
        Button btnSave = findViewById(R.id.btnSaveAddress);
        btnSave.setOnClickListener(v -> {
            String address = editAddress.getText().toString().trim();
            if (address.isEmpty()) {
                Toast.makeText(this, "Введите адрес", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.edit().putString("address", address).apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}