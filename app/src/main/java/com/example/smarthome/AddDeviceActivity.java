package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smarthome.api.ApiClient;
import com.example.smarthome.api.SupabaseApi;
import com.example.smarthome.models.Device;
import com.google.android.material.button.MaterialButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddDeviceActivity extends AppCompatActivity {

    private EditText editDeviceName, editDeviceId;
    private MaterialButton btnCoffee, btnDishwasher;
    private String selectedDeviceType = "coffee";   // "coffee" или "dishwasher"
    private SharedPreferences prefs;
    private int roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        roomId = getIntent().getIntExtra("room_id", -1);
        if (roomId == -1) {
            finish();
            return;
        }

        editDeviceName = findViewById(R.id.editDeviceName);
        editDeviceId = findViewById(R.id.editDeviceId);
        btnCoffee = findViewById(R.id.btnLight);      // используем существующие id
        btnDishwasher = findViewById(R.id.btnAC);
        MaterialButton btnSave = findViewById(R.id.btnSaveAddress);

        // скрываем ненужные кнопки
        findViewById(R.id.btnHood).setVisibility(View.GONE);
        findViewById(R.id.btnTemp).setVisibility(View.GONE);
        findViewById(R.id.btnFan).setVisibility(View.GONE);

        btnCoffee.setText("Кофеварка");
        btnDishwasher.setText("Посудомойка");

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        View.OnClickListener typeListener = v -> {
            resetButtonsBackground();
            MaterialButton btn = (MaterialButton) v;
            btn.setBackgroundTintList(getColorStateList(R.color.blue_selected));
            if (btn.getText().toString().equals("Кофеварка")) {
                selectedDeviceType = "coffee";
            } else {
                selectedDeviceType = "dishwasher";
            }
        };
        btnCoffee.setOnClickListener(typeListener);
        btnDishwasher.setOnClickListener(typeListener);

        btnSave.setOnClickListener(v -> {
            String name = editDeviceName.getText().toString().trim();
            String uid = editDeviceId.getText().toString().trim();
            if (name.isEmpty() || uid.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            saveDeviceToSupabase(name, uid);
        });
    }

    private void saveDeviceToSupabase(String name, String deviceUid) {
        Device device = new Device();
        device.room_id = roomId;
        device.name = name;
        device.device_uid = deviceUid;
        device.type = selectedDeviceType;
        device.is_on = false;

        // начальные параметры в зависимости от типа
        if (selectedDeviceType.equals("coffee")) {
            device.strength = 50;
            device.volume = 200;
        } else {
            device.mode = 50;
            device.temperature = 50;
        }

        SupabaseApi api = ApiClient.getApi();
        api.addDevice(device).enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddDeviceActivity.this, "Устройство добавлено", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    showError("Ошибка добавления устройства");
                }
            }
            @Override
            public void onFailure(Call<Device> call, Throwable t) {
                showError("Нет соединения с сервером");
            }
        });
    }

    private void resetButtonsBackground() {
        int defaultColor = getColor(R.color.gray_button);
        btnCoffee.setBackgroundTintList(getColorStateList(defaultColor));
        btnDishwasher.setBackgroundTintList(getColorStateList(defaultColor));
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