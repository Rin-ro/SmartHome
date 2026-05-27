package com.example.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.smarthome.api.ApiClient;
import com.example.smarthome.api.SupabaseApi;
import com.example.smarthome.models.Profile;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private EditText editUsername, editEmail, editAddress;
    private ImageView imgProfile;
    private SharedPreferences prefs;
    private String userId;
    private Uri selectedAvatarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editAddress = findViewById(R.id.editAddress);
        imgProfile = findViewById(R.id.imgProfile);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnLogout = findViewById(R.id.btnLogout);
        ImageView btnBack = findViewById(R.id.btnBack);

        loadProfile();

        imgProfile.setOnClickListener(v -> openGallery());
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveProfile());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadProfile() {
        SupabaseApi api = ApiClient.getApi();
        api.getProfile(userId).enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Profile p = response.body().get(0);
                    editUsername.setText(p.username);
                    editEmail.setText(p.email);
                    editAddress.setText(p.address);
                    if (p.avatar_url != null && !p.avatar_url.isEmpty()) {
                        Glide.with(ProfileActivity.this).load(p.avatar_url).into(imgProfile);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) { showError("Ошибка загрузки профиля"); }
        });
    }

    private void saveProfile() {
        String username = editUsername.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        if (username.isEmpty() || email.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        Profile profile = new Profile();
        profile.id = userId;
        profile.username = username;
        profile.email = email;
        profile.address = address;
        // avatar_url пока не обновляем (для простоты)
        SupabaseApi api = ApiClient.getApi();
        api.upsertProfile(profile).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ProfileActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override public void onFailure(Call<Void> call, Throwable t) { showError("Ошибка сохранения"); }
        });
    }

    private void logout() {
        prefs.edit().clear().apply();
        ApiClient.setAuthToken(null);
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedAvatarUri = result.getData().getData();
                    imgProfile.setImageURI(selectedAvatarUri);
                    // Здесь можно загрузить файл в Supabase Storage, но для краткости опускаем
                }
            });

    private void showError(String msg) {
        new AlertDialog.Builder(this).setTitle("Ошибка").setMessage(msg)
                .setPositiveButton("OK", null).setCancelable(false).show();
    }
}