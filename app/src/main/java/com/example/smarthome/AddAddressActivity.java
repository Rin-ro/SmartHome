package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddAddressActivity extends AppCompatActivity {

    private EditText addressEdit;
    private SupabaseClient supabaseClient;
    private SharedPreferences prefs;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        supabaseClient = SupabaseClient.getInstance();
        prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        userId = prefs.getString("user_id", "");

        addressEdit = findViewById(R.id.editAddress);
        Button saveBtn = findViewById(R.id.btnSaveAddress);
        saveBtn.setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        String address = addressEdit.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(this, "Введите адрес", Toast.LENGTH_SHORT).show();
            return;
        }
        supabaseClient.updateUserAddress(userId, address, new SupabaseClient.SupabaseCallback() {
            @Override
            public void onSuccess(int responseCode, String response) {
                runOnUiThread(() -> {
                    if (responseCode == 200 || responseCode == 204) {
                        prefs.edit().putString("user_address", address).apply();
                        startActivity(new Intent(AddAddressActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(AddAddressActivity.this, "Не удалось сохранить адрес", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(AddAddressActivity.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}