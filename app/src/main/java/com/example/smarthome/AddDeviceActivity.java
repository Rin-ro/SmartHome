package com.example.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class AddDeviceActivity extends AppCompatActivity {

    private EditText editDeviceName, editDeviceId;
    private MaterialButton btnLight, btnAC, btnHood, btnTemp, btnFan;
    private String selectedDeviceType = "Свет";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        editDeviceName = findViewById(R.id.editDeviceName);
        editDeviceId = findViewById(R.id.editDeviceId);
        btnLight = findViewById(R.id.btnLight);
        btnAC = findViewById(R.id.btnAC);
        btnHood = findViewById(R.id.btnHood);
        btnTemp = findViewById(R.id.btnTemp);
        btnFan = findViewById(R.id.btnFan);
        MaterialButton btnSave = findViewById(R.id.btnSaveAddress);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        View.OnClickListener typeListener = v -> {
            resetButtonsBackground();
            MaterialButton btn = (MaterialButton) v;
            btn.setBackgroundTintList(getColorStateList(R.color.blue_selected));
            selectedDeviceType = btn.getContentDescription().toString();
        };
        btnLight.setOnClickListener(typeListener);
        btnAC.setOnClickListener(typeListener);
        btnHood.setOnClickListener(typeListener);
        btnTemp.setOnClickListener(typeListener);
        btnFan.setOnClickListener(typeListener);

        btnSave.setOnClickListener(v -> {
            String name = editDeviceName.getText().toString().trim();
            String id = editDeviceId.getText().toString().trim();
            if (name.isEmpty() || id.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Устройство \"" + name + "\" добавлено", Toast.LENGTH_SHORT).show();
            finish(); // возврат на экран устройств
        });
    }
    private void resetButtonsBackground() {
        int defaultColor = getColor(R.color.gray_button);
        btnLight.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
        btnAC.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
        btnHood.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
        btnTemp.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
        btnFan.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultColor));
    }
}