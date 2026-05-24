package com.example.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private EditText editUsername, editEmail, editAddress;
    private ImageView imgProfile;
    private SharedPreferences prefs;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editAddress = findViewById(R.id.editAddress);
        imgProfile = findViewById(R.id.imgProfile);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnLogout = findViewById(R.id.btnLogout);
        ImageView btnBack = findViewById(R.id.btnBack);

        loadUserData();

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            imgProfile.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String address = editAddress.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            prefs.edit()
                    .putString("username", username)
                    .putString("email", email)
                    .putString("address", address)
                    .apply();

            Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserData() {
        editUsername.setText(prefs.getString("username", "usernametest"));
        editEmail.setText(prefs.getString("email", "usernametest@mail.ru"));
        editAddress.setText(prefs.getString("address", "г. Омск, ул. Ленина, д. 24"));
    }
}