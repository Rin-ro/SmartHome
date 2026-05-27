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
import com.example.smarthome.models.Room;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private List<Room> rooms = new ArrayList<>();
    private SharedPreferences prefs;
    private TextView addressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        addressTextView = findViewById(R.id.addressTextView);
        recyclerView = findViewById(R.id.roomsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoomAdapter(rooms, room -> {
            Intent intent = new Intent(MainActivity.this, DevicesActivity.class);
            intent.putExtra("room_id", room.id);
            intent.putExtra("room_name", room.name);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        ImageView btnSettings = findViewById(R.id.btnSettings);
        FloatingActionButton fabAddRoom = findViewById(R.id.fabAddRoom);

        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        fabAddRoom.setOnClickListener(v -> startActivity(new Intent(this, AddRoomActivity.class)));

        loadAddress();
        loadRooms();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRooms();
        loadAddress();
    }

    private void loadAddress() {
        String userId = prefs.getString("user_id", "");
        SupabaseApi api = ApiClient.getApi();
        api.getProfile(userId).enqueue(new Callback<List<com.example.smarthome.models.Profile>>() {
            @Override
            public void onResponse(Call<List<com.example.smarthome.models.Profile>> call, Response<List<com.example.smarthome.models.Profile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String addr = response.body().get(0).address;
                    addressTextView.setText(addr != null && !addr.isEmpty() ? addr : "Адрес не указан");
                }
            }
            @Override
            public void onFailure(Call<List<com.example.smarthome.models.Profile>> call, Throwable t) {
                addressTextView.setText("Ошибка загрузки адреса");
            }
        });
    }

    private void loadRooms() {
        String userId = prefs.getString("user_id", "");
        SupabaseApi api = ApiClient.getApi();
        api.getRooms(userId).enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rooms.clear();
                    rooms.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    showError("Не удалось загрузить комнаты");
                }
            }
            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                showError("Нет соединения с сервером");
            }
        });
    }

    private void showError(String msg) {
        new AlertDialog.Builder(this).setTitle("Ошибка").setMessage(msg)
                .setPositiveButton("OK", null).setCancelable(false).show();
    }
}