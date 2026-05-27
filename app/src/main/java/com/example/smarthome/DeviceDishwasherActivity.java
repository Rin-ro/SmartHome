package com.example.smarthome;

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
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceDishwasherActivity extends AppCompatActivity {

    private SwitchCompat switchDishwasher;
    private SeekBar seekBarMode, seekBarTemp;
    private TextView textModeValue, textTempValue, textDishwasherStatus;
    private int deviceId, roomId;
    private Device currentDevice = new Device();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_dishwasher);

        deviceId = getIntent().getIntExtra("device_id", -1);
        roomId = getIntent().getIntExtra("room_id", -1);

        switchDishwasher = findViewById(R.id.switchDishwasher);
        seekBarMode = findViewById(R.id.seekBarMode);
        seekBarTemp = findViewById(R.id.seekBarTemp);
        textModeValue = findViewById(R.id.textModeValue);
        textTempValue = findViewById(R.id.textTempValue);
        textDishwasherStatus = findViewById(R.id.textDishwasherStatus);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        loadDeviceData();

        switchDishwasher.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentDevice.is_on = isChecked;
            textDishwasherStatus.setText(isChecked ? "Включено" : "Выключено");
            updateDevice();
        });

        seekBarMode.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String mode;
                if (progress < 33) mode = "Экономичный";
                else if (progress < 66) mode = "Стандартный";
                else mode = "Интенсивный";
                textModeValue.setText(mode);
                if (fromUser) {
                    currentDevice.mode = progress;
                    updateDevice();
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textTempValue.setText(progress + "°C");
                if (fromUser) {
                    currentDevice.temperature = progress;
                    updateDevice();
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadDeviceData() {
        SupabaseApi api = ApiClient.getApi();
        api.getDevices(roomId).enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Device d : response.body()) {
                        if (d.id == deviceId) {
                            currentDevice = d;
                            switchDishwasher.setChecked(d.is_on);
                            textDishwasherStatus.setText(d.is_on ? "Включено" : "Выключено");
                            seekBarMode.setProgress(d.mode);
                            updateModeText(d.mode);
                            seekBarTemp.setProgress(d.temperature);
                            textTempValue.setText(d.temperature + "°C");
                            break;
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
                showError("Ошибка загрузки данных устройства");
            }
        });
    }

    private void updateDevice() {
        SupabaseApi api = ApiClient.getApi();
        api.updateDevice(deviceId, currentDevice).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {}
            @Override public void onFailure(Call<Void> call, Throwable t) {
                showError("Не удалось сохранить изменения");
            }
        });
    }

    private void updateModeText(int progress) {
        if (progress < 33) textModeValue.setText("Экономичный");
        else if (progress < 66) textModeValue.setText("Стандартный");
        else textModeValue.setText("Интенсивный");
    }

    private void showError(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("Ошибка")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .setCancelable(false)
                .show();
    }
}