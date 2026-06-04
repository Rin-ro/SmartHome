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

public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.ViewHolder> {
    private List<RoomTypes> typeList;
    private OnItemClickListener listener;
    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public RoomTypeAdapter(List<RoomTypes> typeList, OnItemClickListener listener) {
        this.typeList = typeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomTypes type = typeList.get(position);
        holder.textView.setText(type.type_name);
        // Установка иконки (можно по image)
        if (type.image == 1) holder.imageView.setImageResource(R.drawable.living_white1);
        else if (type.image == 2) holder.imageView.setImageResource(R.drawable.kitchen_white1);
        else if (type.image == 3) holder.imageView.setImageResource(R.drawable.bathroom_white1);
        else if (type.image == 4) holder.imageView.setImageResource(R.drawable.office_white1);
        else if (type.image == 5) holder.imageView.setImageResource(R.drawable.bed_white1);
        else if (type.image == 6) holder.imageView.setImageResource(R.drawable.tv_white1);
        else holder.imageView.setImageResource(R.drawable.logo_room);
        // Выделение выбранного
        if (selectedPosition == position) {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.blue_selected));
            holder.textView.setTextColor(holder.itemView.getContext().getColor(R.color.white));
        } else {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.gray_button));
            holder.textView.setTextColor(holder.itemView.getContext().getColor(R.color.black));
        }
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                selectedPosition = pos;
                notifyDataSetChanged();
                if (listener != null) listener.onItemClick(pos);
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
            imageView = itemView.findViewById(R.id.imageRoomType);
            textView = itemView.findViewById(R.id.textRoomType);
            cardView = (CardView) itemView;
        }
    }
}