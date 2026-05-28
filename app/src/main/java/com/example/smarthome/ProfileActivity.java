package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    private EditText nameEdit, emailEdit, addressEdit;
    private ImageView imgProfile;
    private SupabaseClient supabaseClient;
    private String userId;
    private boolean isEditMode = false;
    private static final String AVATAR_FILE_NAME = "profile_avatar.png";

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
        imgProfile = findViewById(R.id.imgProfile);
        MaterialButton btnEditSave = findViewById(R.id.btnEditSave);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        ImageView btnBack = findViewById(R.id.btnBack);

        // Загружаем данные
        nameEdit.setText(prefs.getString("user_name", ""));
        emailEdit.setText(prefs.getString("user_email", ""));
        addressEdit.setText(prefs.getString("user_address", ""));
        loadAvatarFromFile();

        // Режим редактирования: поля неактивны
        setEditMode(false);

        btnBack.setOnClickListener(v -> finish());
        btnEditSave.setOnClickListener(v -> {
            if (isEditMode) {
                saveProfile();
            } else {
                setEditMode(true);
                btnEditSave.setText("Сохранить");
            }
        });
        btnLogout.setOnClickListener(v -> logout());

        imgProfile.setOnClickListener(v -> {
            if (isEditMode) {
                openGallery();
            } else {
                Toast.makeText(this, "Сначала нажмите «Редактировать»", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEditMode(boolean enabled) {
        isEditMode = enabled;
        nameEdit.setEnabled(enabled);
        emailEdit.setEnabled(enabled);
        addressEdit.setEnabled(enabled);
        int bgColor = enabled ? ContextCompat.getColor(this, android.R.color.white) : ContextCompat.getColor(this, android.R.color.darker_gray);
        nameEdit.setBackgroundColor(bgColor);
        emailEdit.setBackgroundColor(bgColor);
        addressEdit.setBackgroundColor(bgColor);
    }

    private void saveProfile() {
        String name = nameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String address = addressEdit.getText().toString().trim();
        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Заполните имя и email", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject data = new JSONObject();
            data.put("name", name);
            data.put("email", email);
            data.put("address", address);
            supabaseClient.updateUserProfile(userId, data, new SupabaseClient.SupabaseCallback() {
                @Override
                public void onSuccess(int code, String resp) {
                    runOnUiThread(() -> {
                        if (code == 200 || code == 204) {
                            SharedPreferences prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("user_name", name)
                                    .putString("user_email", email)
                                    .putString("user_address", address)
                                    .apply();
                            Toast.makeText(ProfileActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
                            setEditMode(false);
                            MaterialButton btnEditSave = findViewById(R.id.btnEditSave);
                            btnEditSave.setText("Редактировать");
                        } else {
                            Toast.makeText(ProfileActivity.this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show());
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void logout() {
        getSharedPreferences("smart_home_prefs", MODE_PRIVATE).edit().clear().apply();
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap src = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                if (src == null) throw new IOException("Не удалось загрузить изображение");
                File file = new File(getFilesDir(), AVATAR_FILE_NAME);
                FileOutputStream fos = new FileOutputStream(file);
                src.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                SharedPreferences prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
                prefs.edit().putString("avatar_path", file.getAbsolutePath()).apply();
                Bitmap circular = makeCircleBitmap(src);
                imgProfile.setImageBitmap(circular);
            } catch (Exception e) {
                Toast.makeText(this, "Не удалось загрузить фото", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadAvatarFromFile() {
        SharedPreferences prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        String avatarPath = prefs.getString("avatar_path", null);
        if (avatarPath != null && new File(avatarPath).exists()) {
            try {
                Bitmap src = BitmapFactory.decodeFile(avatarPath);
                if (src != null) {
                    imgProfile.setImageBitmap(makeCircleBitmap(src));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private Bitmap makeCircleBitmap(Bitmap src) {
        if (src == null) return null;
        int size = Math.min(src.getWidth(), src.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, 0, 0, paint);
        return output;
    }
}