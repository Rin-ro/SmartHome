package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {
    private SupabaseClient supabaseClient;
    private List<Room> roomsList;
    private RecyclerView recyclerView;
    private MainAdapter adapter;
    private TextView addressTextView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        supabaseClient = SupabaseClient.getInstance();
        SharedPreferences prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        addressTextView = findViewById(R.id.addressTextView);
        recyclerView = findViewById(R.id.roomsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomsList = new ArrayList<>();

        // Исправленный адаптер: передаём id и название комнаты
        adapter = new MainAdapter(this, roomsList, position -> {
            int roomId = roomsList.get(position).room_id;
            String roomName = roomsList.get(position).name_room;
            Intent intent = new Intent(MainActivity.this, DevicesActivity.class);
            intent.putExtra("idRoom", roomId);
            intent.putExtra("room_name", roomName);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        FloatingActionButton fabAddRoom = findViewById(R.id.fabAddRoom);
        fabAddRoom.setOnClickListener(v -> startActivity(new Intent(this, AddRoomActivity.class)));
        ImageView btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        loadRooms();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRooms();
        loadAddress();
    }

    private void loadAddress() {
        SharedPreferences prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        String address = prefs.getString("user_address", "");
        addressTextView.setText(address.isEmpty() ? "Адрес не указан" : address);
    }

    private void loadRooms() {
        supabaseClient.getRoomsWithType(userId, new SupabaseClient.SupabaseCallback() {
            @Override
            public void onSuccess(int responseCode, String response) {
                runOnUiThread(() -> {
                    try {
                        Log.d("MainActivity", "Ответ: " + response);
                        roomsList.clear();
                        if (response != null && response.trim().length() > 2 && response.trim().startsWith("[")) {
                            JSONArray rooms = new JSONArray(response);
                            for (int i = 0; i < rooms.length(); i++) {
                                JSONObject obj = rooms.getJSONObject(i);
                                Room room = new Room();
                                room.room_id = obj.getInt("room_id");
                                room.name_room = obj.getString("name_room");
                                if (obj.has("room_types") && !obj.isNull("room_types")) {
                                    JSONObject type = obj.getJSONObject("room_types");
                                    room.name_type = type.getString("name_type");
                                    room.image_room = type.getInt("image_type");
                                } else {
                                    room.name_type = "Неизвестно";
                                    room.image_room = 0;
                                }
                                roomsList.add(room);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if (roomsList.isEmpty()) Toast.makeText(MainActivity.this, "Нет комнат", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) { e.printStackTrace(); }
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}