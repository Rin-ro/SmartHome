package com.example.smarthome;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class DeviceDishwasherActivity extends AppCompatActivity {

    private SwitchCompat switchDishwasher;
    private SeekBar seekBarMode, seekBarTemp;
    private TextView textDishwasherStatus, textModeValue, textTempValue;
    private boolean isOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_dishwasher);

        initViews();
        setupListeners();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        switchDishwasher = findViewById(R.id.switchDishwasher);
        seekBarMode = findViewById(R.id.seekBarMode);
        seekBarTemp = findViewById(R.id.seekBarTemp);
        textDishwasherStatus = findViewById(R.id.textDishwasherStatus);
        textModeValue = findViewById(R.id.textModeValue);
        textTempValue = findViewById(R.id.textTempValue);
    }

    private void setupListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        switchDishwasher.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isOn = isChecked;
            textDishwasherStatus.setText(isChecked ? "Включено" : "Выключено");
            Toast.makeText(this, "Посудомоечная машина " + (isChecked ? "включена" : "выключена"), Toast.LENGTH_SHORT).show();
        });

        seekBarMode.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String mode;
                if (progress < 33) {
                    mode = "Экономичный";
                } else if (progress < 66) {
                    mode = "Стандартный";
                } else {
                    mode = "Интенсивный";
                }
                textModeValue.setText(mode);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(DeviceDishwasherActivity.this, "Режим: " + textModeValue.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        seekBarTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textTempValue.setText(progress + "°C");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(DeviceDishwasherActivity.this, "Температура: " + seekBar.getProgress() + "°C", Toast.LENGTH_SHORT).show();
            }
        });
    }
}