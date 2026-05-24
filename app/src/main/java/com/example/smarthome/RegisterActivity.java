package com.example.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameEditText, emailEditText, passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button registerButton = findViewById(R.id.registerButton);
        Button loginButton = findViewById(R.id.loginButton);
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Некорректный email", Toast.LENGTH_SHORT).show();
                return;
            }
            getSharedPreferences("smart_home_prefs", MODE_PRIVATE)
                    .edit()
                    .putString("username", username)
                    .putString("email", email)
                    .putString("password", password)
                    .apply();

            Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, PinCreateActivity.class));
            finish();
        });
        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, AuthActivity.class));
            finish();
        });
    }
}