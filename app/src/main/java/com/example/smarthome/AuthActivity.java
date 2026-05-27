package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smarthome.api.ApiClient;
import com.example.smarthome.api.SupabaseApi;
import com.example.smarthome.models.AuthResponse;
import com.example.smarthome.models.SignInRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {
    private EditText emailEdit, passEdit;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        emailEdit = findViewById(R.id.emailEditText);
        passEdit = findViewById(R.id.passwordEditText);
        Button loginBtn = findViewById(R.id.loginButton);
        Button regBtn = findViewById(R.id.registerButton);

        loginBtn.setOnClickListener(v -> login());
        regBtn.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email = emailEdit.getText().toString().trim();
        String pass = passEdit.getText().toString().trim();
        if (email.isEmpty() || pass.isEmpty()) {
            showError("Заполните все поля");
            return;
        }
        SupabaseApi api = ApiClient.getApi();
        api.signin(new SignInRequest(email, pass)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().access_token;
                    String userId = response.body().user.id;
                    ApiClient.setAuthToken(token);
                    prefs.edit().putString("user_id", userId).putBoolean("is_logged_in", true).apply();
                    startActivity(new Intent(AuthActivity.this, PinCreateActivity.class));
                    finish();
                } else {
                    showError("Неверный email или пароль");
                }
            }
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                showError("Нет соединения с сервером");
            }
        });
    }

    private void showError(String msg) {
        new AlertDialog.Builder(this).setTitle("Ошибка").setMessage(msg)
                .setPositiveButton("OK", null).setCancelable(false).show();
    }
}