package com.example.smarthome;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.example.smarthome.api.ApiClient;
import com.example.smarthome.api.SupabaseApi;
import com.example.smarthome.models.Device;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceCoffeeActivity extends AppCompatActivity {
    private SwitchCompat switchCoffee;
    private SeekBar seekStrength, seekVolume;
    private TextView textStrength, textVolume, statusText;
    private int deviceId, roomId;
    private Device currentDevice = new Device();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_coffee);
        deviceId = getIntent().getIntExtra("device_id", -1);
        roomId = getIntent().getIntExtra("room_id", -1);
        switchCoffee = findViewById(R.id.switchCoffee);
        seekStrength = findViewById(R.id.seekBarStrength);
        seekVolume = findViewById(R.id.seekBarVolume);
        textStrength = findViewById(R.id.textStrengthValue);
        textVolume = findViewById(R.id.textVolumeValue);
        statusText = findViewById(R.id.textCoffeeStatus);
        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());

        loadDeviceData();

        switchCoffee.setOnCheckedChangeListener((btn, isChecked) -> {
            currentDevice.is_on = isChecked;
            statusText.setText(isChecked ? "Включено" : "Выключено");
            updateDevice();
        });
        seekStrength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textStrength.setText(progress + "%");
                if (fromUser) { currentDevice.strength = progress; updateDevice(); }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textVolume.setText(progress + " мл");
                if (fromUser) { currentDevice.volume = progress; updateDevice(); }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadDeviceData() {
        SupabaseApi api = ApiClient.getApi();
        api.getDevices(roomId).enqueue(new Callback<java.util.List<Device>>() {
            @Override
            public void onResponse(Call<java.util.List<Device>> call, Response<java.util.List<Device>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Device d : response.body()) {
                        if (d.id == deviceId) {
                            currentDevice = d;
                            switchCoffee.setChecked(d.is_on);
                            statusText.setText(d.is_on ? "Включено" : "Выключено");
                            seekStrength.setProgress(d.strength);
                            textStrength.setText(d.strength + "%");
                            seekVolume.setProgress(d.volume);
                            textVolume.setText(d.volume + " мл");
                            break;
                        }
                    }
                }
            }
            @Override public void onFailure(Call<java.util.List<Device>> call, Throwable t) { showError(); }
        });
    }

    private void updateDevice() {
        SupabaseApi api = ApiClient.getApi();
        api.updateDevice(deviceId, currentDevice).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) { }
            @Override public void onFailure(Call<Void> call, Throwable t) { showError(); }
        });
    }

    private void showError() {
        new AlertDialog.Builder(this).setTitle("Ошибка").setMessage("Не удалось сохранить настройки")
                .setPositiveButton("OK", null).setCancelable(false).show();
    }
}