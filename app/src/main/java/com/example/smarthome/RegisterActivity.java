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
import com.example.smarthome.models.Profile;
import com.example.smarthome.models.SignUpRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameEdit, emailEdit, passEdit;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        usernameEdit = findViewById(R.id.usernameEditText);
        emailEdit = findViewById(R.id.emailEditText);
        passEdit = findViewById(R.id.passwordEditText);
        Button regBtn = findViewById(R.id.registerButton);
        Button loginBtn = findViewById(R.id.loginButton);

        regBtn.setOnClickListener(v -> register());
        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        });
    }

    private void register() {
        String username = usernameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String pass = passEdit.getText().toString().trim();
        if (username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showError("Заполните все поля");
            return;
        }
        SupabaseApi api = ApiClient.getApi();
        api.signup(new SignUpRequest(email, pass)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().access_token;
                    String userId = response.body().user.id;
                    ApiClient.setAuthToken(token);
                    // Создаём профиль
                    Profile profile = new Profile();
                    profile.id = userId;
                    profile.username = username;
                    profile.email = email;
                    profile.address = "";
                    api.upsertProfile(profile).enqueue(new Callback<Void>() {
                        @Override public void onResponse(Call<Void> call, Response<Void> response) {}
                        @Override public void onFailure(Call<Void> call, Throwable t) {}
                    });
                    prefs.edit().putString("user_id", userId).apply();
                    startActivity(new Intent(RegisterActivity.this, PinCreateActivity.class));
                    finish();
                } else {
                    showError("Ошибка регистрации. Email может быть занят.");
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