package com.example.smarthome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarthome.models.Device;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    private List<Device> devices;
    private OnItemClickListener listener;
    private OnSwitchChangeListener switchListener;

    public interface OnItemClickListener { void onItemClick(Device device); }
    public interface OnSwitchChangeListener { void onSwitchChanged(Device device, boolean isChecked); }

    public DeviceAdapter(List<Device> devices, OnItemClickListener listener, OnSwitchChangeListener switchListener) {
        this.devices = devices;
        this.listener = listener;
        this.switchListener = switchListener;
    }

    @NonNull @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int pos) {
        Device d = devices.get(pos);
        holder.name.setText(d.name);
        holder.status.setText(d.is_on ? "Включено" : "Выключено");
        holder.switchDevice.setChecked(d.is_on);
        holder.switchDevice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchListener.onSwitchChanged(d, isChecked);
            d.is_on = isChecked;
            holder.status.setText(isChecked ? "Включено" : "Выключено");
        });
        holder.itemView.setOnClickListener(v -> listener.onItemClick(d));
        if (d.type.equals("coffee")) holder.icon.setImageResource(R.drawable.coffee);
        else holder.icon.setImageResource(R.drawable.dish);
    }

    @Override public int getItemCount() { return devices.size(); }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView name, status;
        ImageView icon;
        SwitchCompat switchDevice;
        DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textDeviceName);
            status = itemView.findViewById(R.id.textDeviceStatus);
            icon = itemView.findViewById(R.id.imgDeviceIcon);
            switchDevice = itemView.findViewById(R.id.switchDevice);
        }
    }
}