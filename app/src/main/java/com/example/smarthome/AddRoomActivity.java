package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AddRoomActivity extends AppCompatActivity {
    private SupabaseClient supabaseClient;
    private String userId;
    private int selectedTypeId = -1;
    private EditText roomNameEdit;
    private RecyclerView recyclerView;
    private RoomTypeAdapter adapter;
    private List<RoomTypes> typeList = new ArrayList<>();
    private static final int REQUEST_ADD_CATEGORY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        supabaseClient = SupabaseClient.getInstance();
        SharedPreferences prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        roomNameEdit = findViewById(R.id.editRoomName);
        recyclerView = findViewById(R.id.listRoomTypes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoomTypeAdapter(typeList, position -> {
            selectedTypeId = typeList.get(position).type_id;
        });
        recyclerView.setAdapter(adapter);
        loadRoomTypes();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSaveRoom).setOnClickListener(v -> saveRoom());
        findViewById(R.id.btnAddNewCategory).setOnClickListener(v -> {
            Intent intent = new Intent(AddRoomActivity.this, AddCategoryActivity.class);
            startActivityForResult(intent, REQUEST_ADD_CATEGORY);
        });
    }

    private void loadRoomTypes() {
        supabaseClient.getRoomTypes(userId, new SupabaseClient.SupabaseCallback() {
            @Override
            public void onSuccess(int code, String response) {
                runOnUiThread(() -> {
                    try {
                        typeList.clear();
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            RoomTypes rt = new RoomTypes();
                            rt.type_id = obj.getInt("type_id");
                            rt.type_name = obj.getString("name_type");
                            rt.image = obj.getInt("image_type");
                            if (obj.has("user_id") && !obj.isNull("user_id"))
                                rt.user_id = obj.getString("user_id");
                            typeList.add(rt);
                        }
                        if (!typeList.isEmpty()) {
                            selectedTypeId = typeList.get(0).type_id;
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(AddRoomActivity.this, "Ошибка загрузки типов", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void saveRoom() {
        String roomName = roomNameEdit.getText().toString().trim();
        if (roomName.isEmpty()) {
            Toast.makeText(this, "Введите название комнаты", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedTypeId == -1) {
            Toast.makeText(this, "Выберите тип комнаты", Toast.LENGTH_SHORT).show();
            return;
        }
        roomName = roomName.substring(0,1).toUpperCase() + roomName.substring(1).toLowerCase();
        try {
            JSONObject room = new JSONObject();
            room.put("user_id", userId);
            room.put("name_room", roomName);
            room.put("type_id", selectedTypeId);
            supabaseClient.addRoom(room, new SupabaseClient.SupabaseCallback() {
                @Override public void onSuccess(int code, String resp) {
                    runOnUiThread(() -> {
                        if (code == 201) {
                            Toast.makeText(AddRoomActivity.this, "Комната добавлена", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddRoomActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(AddRoomActivity.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show());
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_CATEGORY && resultCode == RESULT_OK) {
            loadRoomTypes(); // перезагружаем список типов
        }
    }
}