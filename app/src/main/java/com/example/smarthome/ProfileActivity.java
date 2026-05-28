package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private EditText nameEdit, emailEdit, addressEdit;
    private SupabaseClient supabaseClient;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        supabaseClient = SupabaseClient.getInstance();
        SharedPreferences prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        nameEdit = findViewById(R.id.editUsername);
        emailEdit = findViewById(R.id.editEmail);
        addressEdit = findViewById(R.id.editAddress);
        nameEdit.setText(prefs.getString("user_name", ""));
        emailEdit.setText(prefs.getString("user_email", ""));
        addressEdit.setText(prefs.getString("user_address", ""));
        MaterialButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveProfile());
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logout());
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
    private void saveProfile() {
        String name = nameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String address = addressEdit.getText().toString().trim();
        if (name.isEmpty() || email.isEmpty()) { Toast.makeText(this, "Заполните поля", Toast.LENGTH_SHORT).show(); return; }
        try {
            JSONObject data = new JSONObject();
            data.put("name", name);
            data.put("email", email);
            data.put("address", address);
            supabaseClient.updateUserProfile(userId, data, new SupabaseClient.SupabaseCallback() {
                @Override public void onSuccess(int code, String resp) {
                    runOnUiThread(() -> {
                        if (code == 200 || code == 204) {
                            SharedPreferences prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
                            prefs.edit().putString("user_name", name).putString("user_email", email).putString("user_address", address).apply();
                            Toast.makeText(ProfileActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
                            finish();
                        } else Toast.makeText(ProfileActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                    });
                }
                @Override public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Ошибка: "+error, Toast.LENGTH_SHORT).show());
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }
    private void logout() {
        getSharedPreferences("smart_home_prefs", MODE_PRIVATE).edit().clear().apply();
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }
}