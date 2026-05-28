package com.example.smarthome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AllDeviceAdapter extends RecyclerView.Adapter<AllDeviceAdapter.DeviceViewHolder> {
    private List<Device> deviceList;
    private OnItemClickListener listener;
    private LayoutInflater inflater;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public AllDeviceAdapter(Context context, List<Device> deviceList, OnItemClickListener listener) {
        this.deviceList = deviceList;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = deviceList.get(position);
        holder.textName.setText(device.device_type_name);
        holder.textStatus.setText(device.device_work ? "Включено" : "Выключено");
        holder.switchDevice.setChecked(device.device_work);

        // Устанавливаем иконку по названию типа устройства (синие иконки)
        switch (device.device_type_name) {
            case "Кофеварка":
                holder.imgIcon.setImageResource(R.drawable.coffee_blue);
                break;
            case "Посудомоечная машина":
                holder.imgIcon.setImageResource(R.drawable.dish_blue);
                break;
            case "Свет":
                holder.imgIcon.setImageResource(R.drawable.light_blue1);
                break;
            case "Температура":
                holder.imgIcon.setImageResource(R.drawable.thermo_blue1);
                break;
            case "Вентилятор":
                // Если есть синяя иконка вентилятора, используйте её, иначе - временно light_blue1
                holder.imgIcon.setImageResource(R.drawable.fan_white1); // или замените на синюю, если есть
                break;
            case "Вытяжка":
                holder.imgIcon.setImageResource(R.drawable.hood_blue1);
                break;
            case "Кондиционер":
                holder.imgIcon.setImageResource(R.drawable.condi_blue1);
                break;
            default:
                holder.imgIcon.setImageResource(R.drawable.logo_room_blue);
                break;
        }

        // Обработка переключателя
        holder.switchDevice.setOnCheckedChangeListener(null);
        holder.switchDevice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            device.device_work = isChecked;
            holder.textStatus.setText(isChecked ? "Включено" : "Выключено");
            SupabaseClient.getInstance().updateDeviceWorkStatus(String.valueOf(device.device_id), isChecked, new SupabaseClient.SupabaseCallback() {
                @Override public void onSuccess(int code, String resp) { }
                @Override public void onError(String error) { }
            });
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() { return deviceList.size(); }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
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