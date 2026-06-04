package com.example.smarthome;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import org.json.JSONObject;

public class DeviceDishwasherActivity extends AppCompatActivity {
    private SwitchCompat switchDishwasher, switchHalfLoad, switchDryBoost;
    private SeekBar seekBarMode, seekBarTemp;
    private Spinner spinnerProgram, spinnerDelay;
    private TextView textModeValue, textTempValue, textDishwasherStatus, textSaltLevel, textRinseLevel;
    private int deviceId, roomId;
    private SupabaseClient supabaseClient;
    private JSONObject currentParameters = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_dishwasher);
        supabaseClient = SupabaseClient.getInstance();
        deviceId = getIntent().getIntExtra("device_id", -1);
        roomId = getIntent().getIntExtra("room_id", -1);
        if (deviceId == -1 || roomId == -1) { finish(); return; }

        switchDishwasher = findViewById(R.id.switchDishwasher);
        seekBarMode = findViewById(R.id.seekBarMode);
        seekBarTemp = findViewById(R.id.seekBarTemp);
        spinnerProgram = findViewById(R.id.spinnerProgram);
        spinnerDelay = findViewById(R.id.spinnerDelay);
        switchHalfLoad = findViewById(R.id.switchHalfLoad);
        switchDryBoost = findViewById(R.id.switchDryBoost);
        textModeValue = findViewById(R.id.textModeValue);
        textTempValue = findViewById(R.id.textTempValue);
        textDishwasherStatus = findViewById(R.id.textDishwasherStatus);
        textSaltLevel = findViewById(R.id.textSaltLevel);
        textRinseLevel = findViewById(R.id.textRinseLevel);
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Spinner программы
        ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(this,
                R.array.programs, android.R.layout.simple_spinner_item);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProgram.setAdapter(programAdapter);
        // Spinner задержки
        ArrayAdapter<CharSequence> delayAdapter = ArrayAdapter.createFromResource(this,
                R.array.delay_hours, android.R.layout.simple_spinner_item);
        delayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDelay.setAdapter(delayAdapter);

        loadDeviceData();

        switchDishwasher.setOnCheckedChangeListener((btn, isChecked) -> updateDeviceWork(isChecked));
        seekBarMode.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String mode = progress < 33 ? "Экономичный" : (progress < 66 ? "Стандартный" : "Интенсивный");
                textModeValue.setText(mode);
                if (fromUser) { try { currentParameters.put("mode", progress); updateDeviceParameters(); } catch (Exception e) {} }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        seekBarTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int temp = 30 + progress;
                if (temp > 70) temp = 70;
                textTempValue.setText(temp + "°C");
                if (fromUser) { try { currentParameters.put("temperature", temp); updateDeviceParameters(); } catch (Exception e) {} }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        spinnerProgram.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                try { currentParameters.put("program", pos); updateDeviceParameters(); } catch (Exception e) {}
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinnerDelay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                try { currentParameters.put("delay", pos); updateDeviceParameters(); } catch (Exception e) {}
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        switchHalfLoad.setOnCheckedChangeListener((btn, isChecked) -> {
            try { currentParameters.put("halfLoad", isChecked); updateDeviceParameters(); } catch (Exception e) {}
        });
        switchDryBoost.setOnCheckedChangeListener((btn, isChecked) -> {
            try { currentParameters.put("dryBoost", isChecked); updateDeviceParameters(); } catch (Exception e) {}
        });
    }

    private void loadDeviceData() {
        supabaseClient.getDeviceById(String.valueOf(deviceId), new SupabaseClient.SupabaseCallback() {
            @Override public void onSuccess(int code, String response) {
                runOnUiThread(() -> {
                    try {
                        org.json.JSONArray arr = new org.json.JSONArray(response);
                        if (arr.length() > 0) {
                            org.json.JSONObject dev = arr.getJSONObject(0);
                            boolean work = dev.getBoolean("work");
                            String paramsStr = dev.getString("parameters");
                            currentParameters = new org.json.JSONObject(paramsStr);
                            int mode = currentParameters.optInt("mode", 50);
                            int temperature = currentParameters.optInt("temperature", 50);
                            int program = currentParameters.optInt("program", 0);
                            int delay = currentParameters.optInt("delay", 0);
                            boolean halfLoad = currentParameters.optBoolean("halfLoad", false);
                            boolean dryBoost = currentParameters.optBoolean("dryBoost", false);
                            switchDishwasher.setChecked(work);
                            textDishwasherStatus.setText(work ? "Включено" : "Выключено");
                            seekBarMode.setProgress(mode);
                            textModeValue.setText(mode < 33 ? "Экономичный" : (mode < 66 ? "Стандартный" : "Интенсивный"));
                            int tempProgress = temperature - 30;
                            if (tempProgress < 0) tempProgress = 0;
                            seekBarTemp.setProgress(tempProgress);
                            textTempValue.setText(temperature + "°C");
                            spinnerProgram.setSelection(program);
                            spinnerDelay.setSelection(delay);
                            switchHalfLoad.setChecked(halfLoad);
                            switchDryBoost.setChecked(dryBoost);
                            // Имитация индикаторов (можно менять по данным или оставить заглушки)
                            textSaltLevel.setText("Соль: норма");
                            textRinseLevel.setText("Ополаскиватель: норма");
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            }
            @Override public void onError(String error) { showError("Ошибка загрузки"); }
        });
    }

    private void updateDeviceWork(boolean isChecked) {
        supabaseClient.updateDeviceWorkStatus(String.valueOf(deviceId), isChecked, new SupabaseClient.SupabaseCallback() {
            @Override public void onSuccess(int code, String resp) { runOnUiThread(() -> textDishwasherStatus.setText(isChecked ? "Включено" : "Выключено")); }
            @Override public void onError(String error) { runOnUiThread(() -> { switchDishwasher.setChecked(!isChecked); showError("Не удалось изменить состояние"); }); }
        });
    }

    private void updateDeviceParameters() {
        supabaseClient.updateDeviceParameters(String.valueOf(deviceId), currentParameters.toString(), new SupabaseClient.SupabaseCallback() {
            @Override public void onSuccess(int code, String resp) { }
            @Override public void onError(String error) { runOnUiThread(() -> showError("Не удалось сохранить параметры")); }
        });
    }

    private void showError(String msg) {
        new AlertDialog.Builder(this).setTitle("Ошибка").setMessage(msg).setPositiveButton("OK", null).setCancelable(false).show();
    }
}