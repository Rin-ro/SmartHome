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
import com.example.smarthome.models.Profile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                showError("Введите адрес");
                return;
            }
            String userId = prefs.getString("user_id", "");
            SupabaseApi api = ApiClient.getApi();
            Profile profile = new Profile();
            profile.id = userId;
            profile.address = address;
            // остальные поля не обновляем
            api.upsertProfile(profile).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        startActivity(new Intent(AddAddressActivity.this, MainActivity.class));
                        finish();
                    } else {
                        showError("Не удалось сохранить адрес");
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    showError("Нет соединения с сервером");
                }
            });
        });
    }

    private void showError(String msg) {
        new AlertDialog.Builder(this).setTitle("Ошибка").setMessage(msg)
                .setPositiveButton("OK", null).setCancelable(false).show();
    }
}