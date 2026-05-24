package com.example.smarthome;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class DeviceCoffeeActivity extends AppCompatActivity {

    private SwitchCompat switchCoffee;
    private SeekBar seekBarStrength, seekBarVolume;
    private TextView textCoffeeStatus, textStrengthValue, textVolumeValue;
    private boolean isOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_coffee);

        initViews();
        setupListeners();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        switchCoffee = findViewById(R.id.switchCoffee);
        seekBarStrength = findViewById(R.id.seekBarStrength);
        seekBarVolume = findViewById(R.id.seekBarVolume);
        textCoffeeStatus = findViewById(R.id.textCoffeeStatus);
        textStrengthValue = findViewById(R.id.textStrengthValue);
        textVolumeValue = findViewById(R.id.textVolumeValue);
    }

    private void setupListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        switchCoffee.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isOn = isChecked;
            textCoffeeStatus.setText(isChecked ? "Включено" : "Выключено");
            Toast.makeText(this, "Кофеварка " + (isChecked ? "включена" : "выключена"), Toast.LENGTH_SHORT).show();
        });

        seekBarStrength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textStrengthValue.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(DeviceCoffeeActivity.this, "Крепость: " + seekBar.getProgress() + "%", Toast.LENGTH_SHORT).show();
            }
        });

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textVolumeValue.setText(progress + " мл");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(DeviceCoffeeActivity.this, "Объем: " + seekBar.getProgress() + " мл", Toast.LENGTH_SHORT).show();
            }
        });
    }
}