package com.example.smarthome;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import org.json.JSONObject;

public class DeviceCoffeeActivity extends AppCompatActivity {
    private SwitchCompat switchCoffee;
    private SeekBar seekStrength, seekVolume;
    private TextView textStrength, textVolume, statusText;
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
        seekStrength = findViewById(R.id.seekBarStrength);
        seekVolume = findViewById(R.id.seekBarVolume);
        textStrength = findViewById(R.id.textStrengthValue);
        textVolume = findViewById(R.id.textVolumeValue);
        statusText = findViewById(R.id.textCoffeeStatus);
        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
        loadDeviceData();
        switchCoffee.setOnCheckedChangeListener((btn, isChecked) -> updateDeviceWork(isChecked));
        seekStrength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textStrength.setText(progress + "%");
                if (fromUser) { try { currentParameters.put("strength", progress); updateDeviceParameters(); } catch (Exception e) {} }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textVolume.setText(progress + " мл");
                if (fromUser) { try { currentParameters.put("volume", progress); updateDeviceParameters(); } catch (Exception e) {} }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
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
                            int strength = currentParameters.optInt("strength", 50);
                            int volume = currentParameters.optInt("volume", 200);
                            switchCoffee.setChecked(work);
                            statusText.setText(work ? "Включено" : "Выключено");
                            seekStrength.setProgress(strength);
                            textStrength.setText(strength + "%");
                            seekVolume.setProgress(volume);
                            textVolume.setText(volume + " мл");
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