package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarthome.api.ApiClient;
import com.example.smarthome.api.SupabaseApi;
import com.example.smarthome.models.Device;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DevicesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private List<Device> devices = new ArrayList<>();
    private int roomId;
    private String roomName;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        roomId = getIntent().getIntExtra("room_id", -1);
        roomName = getIntent().getStringExtra("room_name");
        TextView title = findViewById(R.id.textRoomName);
        title.setText("Устройства в " + roomName);
        recyclerView = findViewById(R.id.recyclerViewDevices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceAdapter(devices, device -> {
            if (device.type.equals("coffee")) {
                Intent i = new Intent(this, DeviceCoffeeActivity.class);
                i.putExtra("device_id", device.id);
                i.putExtra("room_id", roomId);
                startActivity(i);
            } else if (device.type.equals("dishwasher")) {
                Intent i = new Intent(this, DeviceDishwasherActivity.class);
                i.putExtra("device_id", device.id);
                i.putExtra("room_id", roomId);
                startActivity(i);
            }
        }, (device, isChecked) -> updateDeviceState(device, isChecked));
        recyclerView.setAdapter(adapter);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        FloatingActionButton fab = findViewById(R.id.fabAddDevice);
        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, AddDeviceActivity.class);
            i.putExtra("room_id", roomId);
            startActivity(i);
        });
        loadDevices();
    }

    private void loadDevices() {
        SupabaseApi api = ApiClient.getApi();
        api.getDevices(roomId).enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    devices.clear();
                    devices.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
                showError("Ошибка загрузки устройств");
            }
        });
    }

    private void updateDeviceState(Device device, boolean isOn) {
        device.is_on = isOn;
        SupabaseApi api = ApiClient.getApi();
        api.updateDevice(device.id, device).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) { }
            @Override public void onFailure(Call<Void> call, Throwable t) { showError("Не удалось изменить состояние"); }
        });
    }

    private void showError(String msg) {
        new AlertDialog.Builder(this).setTitle("Ошибка").setMessage(msg)
                .setPositiveButton("OK", null).setCancelable(false).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDevices();
    }
}