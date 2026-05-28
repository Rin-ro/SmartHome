package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AddDeviceActivity extends AppCompatActivity {
    private SupabaseClient supabaseClient;
    private int roomId;
    private EditText editDeviceName, editDeviceId;
    private RecyclerView recyclerView;
    private DeviceTypeAdapter adapter;
    private List<DeviceTypes> deviceTypesList;
    private int selectedDeviceTypeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        supabaseClient = SupabaseClient.getInstance();
        roomId = getIntent().getIntExtra("room_id", 0);
        if (roomId == 0) { Toast.makeText(this, "Ошибка: комната не указана", Toast.LENGTH_SHORT).show(); finish(); return; }
        editDeviceName = findViewById(R.id.editDeviceName);
        editDeviceId = findViewById(R.id.editDeviceId);
        recyclerView = findViewById(R.id.listDevice);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceTypesList = new ArrayList<>();
        // Добавляем все типы устройств
        deviceTypesList.add(new DeviceTypes("Свет", 101)); // неактивный
        deviceTypesList.add(new DeviceTypes("Температура", 102));
        deviceTypesList.add(new DeviceTypes("Вентилятор", 103));
        deviceTypesList.add(new DeviceTypes("Вытяжка", 104));
        deviceTypesList.add(new DeviceTypes("Кондиционер", 105));
        deviceTypesList.add(new DeviceTypes("Кофеварка", 1));
        deviceTypesList.add(new DeviceTypes("Посудомоечная машина", 2));
        adapter = new DeviceTypeAdapter(deviceTypesList, position -> {
            selectedDeviceTypeId = deviceTypesList.get(position).image;
        });
        recyclerView.setAdapter(adapter);
        MaterialButton btnSave = findViewById(R.id.btnSaveDevice);
        btnSave.setOnClickListener(v -> saveDevice());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
    private void saveDevice() {
        String deviceName = editDeviceName.getText().toString().trim();
        String deviceUid = editDeviceId.getText().toString().trim();
        if (deviceName.isEmpty() || deviceUid.isEmpty()) { Toast.makeText(this, "Заполните поля", Toast.LENGTH_SHORT).show(); return; }
        if (selectedDeviceTypeId == -1) { Toast.makeText(this, "Выберите тип", Toast.LENGTH_SHORT).show(); return; }
        // Проверяем, что выбранный тип – кофеварка (1) или посудомойка (2)
        if (selectedDeviceTypeId != 1 && selectedDeviceTypeId != 2) {
            Toast.makeText(this, "Можно добавить только кофеварку или посудомойку", Toast.LENGTH_SHORT).show();
            return;
        }
        deviceName = deviceName.substring(0,1).toUpperCase() + deviceName.substring(1).toLowerCase();
        String parameters = "";
        try {
            JSONObject params = new JSONObject();
            if (selectedDeviceTypeId == 1) { params.put("strength", 50); params.put("volume", 200); }
            else { params.put("mode", 50); params.put("temperature", 50); }
            parameters = params.toString();
        } catch (Exception e) { e.printStackTrace(); }
        try {
            JSONObject device = new JSONObject();
            device.put("room_id", roomId);
            device.put("device_type_id", selectedDeviceTypeId);
            device.put("work", false);
            device.put("parameters", parameters);
            supabaseClient.addDevice(device, new SupabaseClient.SupabaseCallback() {
                @Override public void onSuccess(int code, String resp) {
                    runOnUiThread(() -> {
                        if (code == 201) { Toast.makeText(AddDeviceActivity.this, "Устройство добавлено", Toast.LENGTH_SHORT).show(); finish(); }
                        else Toast.makeText(AddDeviceActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                    });
                }
                @Override public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(AddDeviceActivity.this, "Ошибка: "+error, Toast.LENGTH_SHORT).show());
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }
}