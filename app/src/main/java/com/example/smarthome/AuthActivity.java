package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;

public class AuthActivity extends AppCompatActivity {
    private EditText emailEdit, passwordEdit;
    private SupabaseClient supabaseClient;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        supabaseClient = SupabaseClient.getInstance();
        prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);

        emailEdit = findViewById(R.id.emailEditText);
        passwordEdit = findViewById(R.id.passwordEditText);
        Button loginBtn = findViewById(R.id.loginButton);
        Button registerBtn = findViewById(R.id.registerButton);

        loginBtn.setOnClickListener(v -> login());
        registerBtn.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        supabaseClient.loginUser(email, password, new SupabaseClient.SupabaseCallback() {
            @Override
            public void onSuccess(int responseCode, String response) {
                runOnUiThread(() -> {
                    try {
                        if (responseCode == 200 && response.length() > 2) {
                            JSONArray users = new JSONArray(response);
                            if (users.length() > 0) {
                                JSONObject user = users.getJSONObject(0);
                                int userId = user.getInt("user_id");
                                String userName = user.getString("name");
                                String userEmail = user.getString("email");
                                String userAddress = user.optString("address", "");

                                prefs.edit()
                                        .putString("user_id", String.valueOf(userId))
                                        .putString("user_name", userName)
                                        .putString("user_email", userEmail)
                                        .putString("user_address", userAddress)
                                        .apply();

                                if (userAddress.isEmpty()) {
                                    startActivity(new Intent(AuthActivity.this, AddAddressActivity.class));
                                } else {
                                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                }
                                finish();
                            } else {
                                Toast.makeText(AuthActivity.this, "Неверный email или пароль", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AuthActivity.this, "Ошибка входа", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(AuthActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(AuthActivity.this, "Сетевая ошибка: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}