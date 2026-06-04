package com.example.smarthome;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import org.json.JSONObject;

public class DeviceCoffeeActivity extends AppCompatActivity {
    private SwitchCompat switchCoffee, switchPreWet;
    private SeekBar seekStrength, seekVolume, seekTemp;
    private RadioGroup radioGroupGrind;
    private Spinner spinnerPortions;
    private TextView textStrength, textVolume, textTemp, statusText;
    private int deviceId, roomId;
    private SupabaseClient supabaseClient;
    private JSONObject currentParameters = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_coffee);
        supabaseClient = SupabaseClient.getInstance();
        deviceId = getIntent().getIntExtra("device_id", -1);
        roomId = getIntent().getIntExtra("room_id", -1);
        if (deviceId == -1 || roomId == -1) { finish(); return; }

        switchCoffee = findViewById(R.id.switchCoffee);
        switchPreWet = findViewById(R.id.switchPreWet);
        seekStrength = findViewById(R.id.seekBarStrength);
        seekVolume = findViewById(R.id.seekBarVolume);
        seekTemp = findViewById(R.id.seekBarTemp);
        radioGroupGrind = findViewById(R.id.radioGroupGrind);
        spinnerPortions = findViewById(R.id.spinnerPortions);
        textStrength = findViewById(R.id.textStrengthValue);
        textVolume = findViewById(R.id.textVolumeValue);
        textTemp = findViewById(R.id.textTempValue);
        statusText = findViewById(R.id.textCoffeeStatus);
        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());

        // Spinner порций
        ArrayAdapter<CharSequence> portionsAdapter = ArrayAdapter.createFromResource(this,
                R.array.portions, android.R.layout.simple_spinner_item);
        portionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPortions.setAdapter(portionsAdapter);

        loadDeviceData();

        switchCoffee.setOnCheckedChangeListener((btn, isChecked) -> updateDeviceWork(isChecked));
        seekStrength.setOnSeekBarChangeListener(createSeekListener("strength", textStrength));
        seekVolume.setOnSeekBarChangeListener(createSeekListener("volume", textVolume));
        seekTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int temp = 70 + progress; // диапазон 70-90
                textTemp.setText(temp + "°C");
                if (fromUser) {
                    try { currentParameters.put("temperature", temp); updateDeviceParameters(); } catch (Exception e) {}
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        radioGroupGrind.setOnCheckedChangeListener((group, checkedId) -> {
            String grind = "";
            if (checkedId == R.id.radioGrindFine) grind = "fine";
            else if (checkedId == R.id.radioGrindMedium) grind = "medium";
            else if (checkedId == R.id.radioGrindCoarse) grind = "coarse";
            try { currentParameters.put("grind", grind); updateDeviceParameters(); } catch (Exception e) {}
        });
        switchPreWet.setOnCheckedChangeListener((btn, isChecked) -> {
            try { currentParameters.put("preWet", isChecked); updateDeviceParameters(); } catch (Exception e) {}
        });
        spinnerPortions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                try { currentParameters.put("portions", pos + 1); updateDeviceParameters(); } catch (Exception e) {}
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private SeekBar.OnSeekBarChangeListener createSeekListener(String key, TextView tv) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String suffix = key.equals("volume") ? " мл" : "%";
                tv.setText(progress + suffix);
                if (fromUser) {
                    try { currentParameters.put(key, progress); updateDeviceParameters(); } catch (Exception e) {}
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        };
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
                            int strength = currentParameters.optInt("strength", 50);
                            int volume = currentParameters.optInt("volume", 200);
                            int temperature = currentParameters.optInt("temperature", 90);
                            String grind = currentParameters.optString("grind", "medium");
                            boolean preWet = currentParameters.optBoolean("preWet", false);
                            int portions = currentParameters.optInt("portions", 1);
                            switchCoffee.setChecked(work);
                            statusText.setText(work ? "Включено" : "Выключено");
                            seekStrength.setProgress(strength);
                            textStrength.setText(strength + "%");
                            seekVolume.setProgress(volume);
                            textVolume.setText(volume + " мл");
                            int tempProgress = temperature - 70;
                            if (tempProgress < 0) tempProgress = 0;
                            seekTemp.setProgress(tempProgress);
                            textTemp.setText(temperature + "°C");
                            if (grind.equals("fine")) radioGroupGrind.check(R.id.radioGrindFine);
                            else if (grind.equals("coarse")) radioGroupGrind.check(R.id.radioGrindCoarse);
                            else radioGroupGrind.check(R.id.radioGrindMedium);
                            switchPreWet.setChecked(preWet);
                            spinnerPortions.setSelection(portions - 1);
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            }
            @Override public void onError(String error) { showError("Ошибка загрузки"); }
        });
    }

    private void updateDeviceWork(boolean isChecked) {
        supabaseClient.updateDeviceWorkStatus(String.valueOf(deviceId), isChecked, new SupabaseClient.SupabaseCallback() {
            @Override public void onSuccess(int code, String resp) { runOnUiThread(() -> statusText.setText(isChecked ? "Включено" : "Выключено")); }
            @Override public void onError(String error) { runOnUiThread(() -> { switchCoffee.setChecked(!isChecked); showError("Не удалось изменить состояние"); }); }
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