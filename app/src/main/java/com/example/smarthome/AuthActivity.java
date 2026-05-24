package com.example.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class AuthActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Некорректный email", Toast.LENGTH_SHORT).show();
                return;
            }
            getSharedPreferences("smart_home_prefs", MODE_PRIVATE)
                    .edit().putString("email", email).apply();
            startActivity(new Intent(AuthActivity.this, PinCreateActivity.class));
            finish();
        });
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(AuthActivity.this, RegisterActivity.class));
        });
    }
}