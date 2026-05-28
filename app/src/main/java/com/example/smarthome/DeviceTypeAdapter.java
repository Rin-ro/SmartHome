package com.example.smarthome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DeviceTypeAdapter extends RecyclerView.Adapter<DeviceTypeAdapter.ViewHolder> {
    private List<DeviceTypes> typeList;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public interface OnItemClickListener { void onItemClick(int position); }

    public DeviceTypeAdapter(List<DeviceTypes> typeList, OnItemClickListener listener) {
        this.typeList = typeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeviceTypes type = typeList.get(position);
        holder.textView.setText(type.type_name);

        // Иконка по типу устройства
        switch (type.type_name) {
            case "Свет": holder.imageView.setImageResource(R.drawable.light_white1); break;
            case "Температура": holder.imageView.setImageResource(R.drawable.thermo_white1); break;
            case "Вентилятор": holder.imageView.setImageResource(R.drawable.fan_white1); break;
            case "Вытяжка": holder.imageView.setImageResource(R.drawable.hood_blue1); break;
            case "Кондиционер": holder.imageView.setImageResource(R.drawable.condi_blue1); break;
            case "Кофеварка": holder.imageView.setImageResource(R.drawable.coffee); break;
            case "Посудомоечная машина": holder.imageView.setImageResource(R.drawable.dish); break;
            default: holder.imageView.setImageResource(R.drawable.logo_room);
        }

        // Выделение выбранного элемента
        if (selectedPosition == position) {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.blue_selected));
            holder.textView.setTextColor(holder.itemView.getContext().getColor(R.color.white));
        } else {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.white));
            holder.textView.setTextColor(holder.itemView.getContext().getColor(R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;
            DeviceTypes currentType = typeList.get(adapterPosition);

            // Активны только кофеварка и посудомойка
            if (currentType.type_name.equals("Кофеварка") || currentType.type_name.equals("Посудомоечная машина")) {
                selectedPosition = adapterPosition;
                notifyDataSetChanged();
                if (listener != null) listener.onItemClick(adapterPosition);
            } else {
                Toast.makeText(v.getContext(), "Это устройство временно недоступно для добавления", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() { return typeList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        CardView cardView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgDeviceIcon);
            textView = itemView.findViewById(R.id.textDeviceName);
            cardView = (CardView) itemView;
        }
    }
}