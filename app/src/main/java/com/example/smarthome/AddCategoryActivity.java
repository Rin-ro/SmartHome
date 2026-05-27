package com.example.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

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
                Intent result = new Intent();
                result.putExtra("category", cat);
                setResult(RESULT_OK, result);
                finish();
            } else {
                Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}