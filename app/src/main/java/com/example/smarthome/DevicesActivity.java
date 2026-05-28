package com.example.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DevicesActivity extends AppCompatActivity {
    private SupabaseClient supabaseClient;
    private int roomId;
    private String roomName;
    private List<Device> deviceList;
    private RecyclerView recyclerView;
    private AllDeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        supabaseClient = SupabaseClient.getInstance();
        roomId = getIntent().getIntExtra("idRoom", 0);
        roomName = getIntent().getStringExtra("room_name");
        TextView title = findViewById(R.id.textRoomName);
        title.setText("Устройства в " + (roomName == null ? "комнате" : roomName));
        recyclerView = findViewById(R.id.recyclerViewDevices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceList = new ArrayList<>();
        adapter = new AllDeviceAdapter(this, deviceList, position -> {
            Device dev = deviceList.get(position);
            if (dev.device_type_name.equals("Кофеварка")) {
                Intent i = new Intent(DevicesActivity.this, DeviceCoffeeActivity.class);
                i.putExtra("device_id", dev.device_id);
                i.putExtra("room_id", roomId);
                startActivity(i);
            } else if (dev.device_type_name.equals("Посудомоечная машина")) {
                Intent i = new Intent(DevicesActivity.this, DeviceDishwasherActivity.class);
                i.putExtra("device_id", dev.device_id);
                i.putExtra("room_id", roomId);
                startActivity(i);
            }
        });
        recyclerView.setAdapter(adapter);
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        FloatingActionButton fabAddDevice = findViewById(R.id.fabAddDevice);
        fabAddDevice.setOnClickListener(v -> {
            Intent i = new Intent(DevicesActivity.this, AddDeviceActivity.class);
            i.putExtra("room_id", roomId);
            startActivity(i);
        });
        loadDevices();
    }

    private void loadDevices() {
        supabaseClient.getDevicesByRoomIdWithType(String.valueOf(roomId), new SupabaseClient.SupabaseCallback() {
            @Override
            public void onSuccess(int responseCode, String response) {
                runOnUiThread(() -> {
                    try {
                        deviceList.clear();
                        if (response != null && response.length() > 3) {
                            JSONArray arr = new JSONArray(response);
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                Device d = new Device();
                                d.device_id = obj.getInt("device_id");
                                d.parameters = obj.getString("parameters");
                                d.device_work = obj.getBoolean("work");
                                JSONObject type = obj.getJSONObject("device_types");
                                d.device_type_name = type.getString("device_name_type");
                                d.device_image = type.getInt("device_image_type");
                                deviceList.add(d);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(DevicesActivity.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}