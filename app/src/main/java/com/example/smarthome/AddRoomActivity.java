package com.example.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class AddRoomActivity extends AppCompatActivity {

    private EditText editRoomName;
    private MaterialButton btnLiving, btnKitchen, btnBathroom, btnStudy, btnBedroom, btnHall;
    private String selectedRoomType = "Гостиная";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        editRoomName = findViewById(R.id.editRoomName);
        btnLiving = findViewById(R.id.btnLiving);
        btnKitchen = findViewById(R.id.btnKitchen);
        btnBathroom = findViewById(R.id.btnBathroom);
        btnStudy = findViewById(R.id.btnStudy);
        btnBedroom = findViewById(R.id.btnBedroom);
        btnHall = findViewById(R.id.btnHall);
        MaterialButton btnSave = findViewById(R.id.btnSaveRoom);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        View.OnClickListener typeListener = v -> {
            resetButtonsBackground();
            MaterialButton btn = (MaterialButton) v;
            btn.setBackgroundTintList(getColorStateList(R.color.blue_selected));
            selectedRoomType = btn.getContentDescription().toString();
        };

        btnLiving.setOnClickListener(typeListener);
        btnKitchen.setOnClickListener(typeListener);
        btnBathroom.setOnClickListener(typeListener);
        btnStudy.setOnClickListener(typeListener);
        btnBedroom.setOnClickListener(typeListener);
        btnHall.setOnClickListener(typeListener);

        btnSave.setOnClickListener(v -> {
            String roomName = editRoomName.getText().toString().trim();
            if (roomName.isEmpty()) {
                Toast.makeText(this, "Введите название комнаты", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Комната \"" + roomName + "\" добавлена", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
    private void resetButtonsBackground() {
        btnLiving.setBackgroundTintList(getColorStateList(R.color.gray_button));
        btnKitchen.setBackgroundTintList(getColorStateList(R.color.gray_button));
        btnBathroom.setBackgroundTintList(getColorStateList(R.color.gray_button));
        btnStudy.setBackgroundTintList(getColorStateList(R.color.gray_button));
        btnBedroom.setBackgroundTintList(getColorStateList(R.color.gray_button));
        btnHall.setBackgroundTintList(getColorStateList(R.color.gray_button));
    }
}