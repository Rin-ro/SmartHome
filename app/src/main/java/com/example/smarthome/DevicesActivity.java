package com.example.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class DevicesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private List<Device> deviceList;
    private TextView textRoomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        textRoomName = findViewById(R.id.textRoomName);
        recyclerView = findViewById(R.id.recyclerViewDevices);
        ImageView btnBack = findViewById(R.id.btnBack);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddDevice);

        String roomName = getIntent().getStringExtra("room_name");
        if (roomName == null) roomName = "Комната";
        textRoomName.setText("Устройства в " + roomName);

        deviceList = new ArrayList<>();
        deviceList.add(new Device("Кофеварка", false, R.drawable.coffee));
        deviceList.add(new Device("Посудомоечная машина", true, R.drawable.dish));

        adapter = new DeviceAdapter(deviceList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(DevicesActivity.this, AddDeviceActivity.class)));
    }

    private static class Device {
        String name;
        boolean isOn;
        int iconRes;

        Device(String name, boolean isOn, int iconRes) {
            this.name = name;
            this.isOn = isOn;
            this.iconRes = iconRes;
        }
    }

    private class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder> {
        private List<Device> devices;

        DeviceAdapter(List<Device> devices) {
            this.devices = devices;
        }

        @NonNull
        @Override
        public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
            return new DeviceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
            Device device = devices.get(position);
            holder.textName.setText(device.name);
            holder.textStatus.setText(device.isOn ? "Включено" : "Выключено");
            holder.switchDevice.setChecked(device.isOn);
            holder.imgIcon.setImageResource(device.iconRes);

            holder.switchDevice.setOnCheckedChangeListener((buttonView, isChecked) -> {
                device.isOn = isChecked;
                holder.textStatus.setText(isChecked ? "Включено" : "Выключено");
                Toast.makeText(DevicesActivity.this, device.name + (isChecked ? " включена" : " выключена"), Toast.LENGTH_SHORT).show();
            });

            holder.itemView.setOnClickListener(v -> {
                if (device.name.equals("Кофеварка")) {
                    startActivity(new Intent(DevicesActivity.this, DeviceCoffeeActivity.class));
                } else if (device.name.equals("Посудомоечная машина")) {
                    startActivity(new Intent(DevicesActivity.this, DeviceDishwasherActivity.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }
    }

    private static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textStatus;
        ImageView imgIcon;
        SwitchCompat switchDevice;

        DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textDeviceName);
            textStatus = itemView.findViewById(R.id.textDeviceStatus);
            imgIcon = itemView.findViewById(R.id.imgDeviceIcon);
            switchDevice = itemView.findViewById(R.id.switchDevice);
        }
    }
}