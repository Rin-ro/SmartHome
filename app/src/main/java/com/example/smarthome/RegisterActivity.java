package com.example.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameEdit, emailEdit, passwordEdit;
    private SupabaseClient supabaseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        supabaseClient = SupabaseClient.getInstance();

        nameEdit = findViewById(R.id.usernameEditText);
        emailEdit = findViewById(R.id.emailEditText);
        passwordEdit = findViewById(R.id.passwordEditText);
        Button regBtn = findViewById(R.id.registerButton);
        Button loginBtn = findViewById(R.id.loginButton);

        regBtn.setOnClickListener(v -> register());
        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        });
    }

    private void register() {
        String name = nameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Некорректный email", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject user = new JSONObject();
            user.put("name", name);
            user.put("email", email);
            user.put("password", password);
            user.put("address", "");
            supabaseClient.addUser(user, new SupabaseClient.SupabaseCallback() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    runOnUiThread(() -> {
                        if (responseCode == 201) {
                            Toast.makeText(RegisterActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, AuthActivity.class));
                            finish();
                        } else if (responseCode == 409) {
                            Toast.makeText(RegisterActivity.this, "Пользователь с таким email уже существует", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Ошибка регистрации: " + responseCode, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show());
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}