package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import org.json.JSONObject;

public class AddCategoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        EditText edit = findViewById(R.id.editCategoryName);
        MaterialButton save = findViewById(R.id.btnSaveCategory);
        save.setOnClickListener(v -> {
            String cat = edit.getText().toString().trim();
            if (!cat.isEmpty()) {
                saveCategoryToSupabase(cat);
            } else {
                Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void saveCategoryToSupabase(String categoryName) {
        SharedPreferences prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", "");
        if (userId.isEmpty()) {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject type = new JSONObject();
            type.put("name_type", categoryName);
            type.put("image_type", 7); // иконка "другое" (logo_room)
            type.put("user_id", userId);
            SupabaseClient.getInstance().addRoomType(type, new SupabaseClient.SupabaseCallback() {
                @Override
                public void onSuccess(int code, String resp) {
                    runOnUiThread(() -> {
                        if (code == 201) {
                            Toast.makeText(AddCategoryActivity.this, "Категория добавлена", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(AddCategoryActivity.this, "Ошибка: " + code, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(AddCategoryActivity.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show());
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }
}