package com.example.smarthome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DeviceTypeAdapter extends RecyclerView.Adapter<DeviceTypeAdapter.ViewHolder> {
    private List<DeviceTypes> typeList;
    private OnItemClickListener listener;
    private int selectedPosition = 0;
    public interface OnItemClickListener { void onItemClick(int position); }
    public DeviceTypeAdapter(List<DeviceTypes> typeList, OnItemClickListener listener) {
        this.typeList = typeList;
        this.listener = listener;
    }
    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_type, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeviceTypes type = typeList.get(position);
        holder.textView.setText(type.type_name);
        if (type.type_name.equals("Кофеварка")) holder.imageView.setImageResource(R.drawable.coffee);
        else holder.imageView.setImageResource(R.drawable.dish);
        if (selectedPosition == position) {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.blue_selected));
            holder.textView.setTextColor(holder.itemView.getContext().getColor(R.color.white));
        } else {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.white));
            holder.textView.setTextColor(holder.itemView.getContext().getColor(R.color.black));
        }
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
            if (listener != null) listener.onItemClick(selectedPosition);
        });
    }
    @Override public int getItemCount() { return typeList.size(); }
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView; TextView textView; CardView cardView;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgDeviceIcon);
            textView = itemView.findViewById(R.id.textDeviceName);
            cardView = (CardView) itemView;
        }
    }
}