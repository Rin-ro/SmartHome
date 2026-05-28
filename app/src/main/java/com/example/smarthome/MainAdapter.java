package com.example.smarthome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private int[] Pictures = {
            R.drawable.living_blue1,   // 1
            R.drawable.kitchen_blue1,  // 2
            R.drawable.bathroom_blue1, // 3
            R.drawable.office_blue1,   // 4
            R.drawable.beg_blue1,      // 5
            R.drawable.tv_blue1,       // 6
            R.drawable.logo_room_blue  // 7
    };
    private List<Room> roomsList;
    private OnItemClickListener listener;
    private LayoutInflater inflater;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public MainAdapter(Context context, List<Room> roomsList, OnItemClickListener listener) {
        this.roomsList = roomsList;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_room, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        Room room = roomsList.get(position);
        holder.textViewName.setText(room.name_room);
        // image_room в БД начинается с 1, а массив с 0 – вычитаем 1
        int iconIndex = room.image_room - 1;
        if (iconIndex < 0 || iconIndex >= Pictures.length) iconIndex = 0;
        holder.imageView.setImageResource(Pictures[iconIndex]);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() { return roomsList.size(); }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;
        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.roomIconImageView);
            textViewName = itemView.findViewById(R.id.roomNameTextView);
        }
    }
}