package com.example.smarthome;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import org.json.JSONObject;

public class AddRoomActivity extends AppCompatActivity {
    private SupabaseClient supabaseClient;
    private String userId;
    private int selectedTypeId = -1;
    private EditText roomNameEdit;
    private MaterialButton btnLiving, btnKitchen, btnBathroom, btnStudy, btnBedroom, btnHall, btnOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        supabaseClient = SupabaseClient.getInstance();
        SharedPreferences prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        roomNameEdit = findViewById(R.id.editRoomName);
        btnLiving = findViewById(R.id.btnLiving);
        btnKitchen = findViewById(R.id.btnKitchen);
        btnBathroom = findViewById(R.id.btnBathroom);
        btnStudy = findViewById(R.id.btnStudy);
        btnBedroom = findViewById(R.id.btnBedroom);
        btnHall = findViewById(R.id.btnHall);
        btnOther = findViewById(R.id.btnOther);
        btnLiving.setOnClickListener(v -> selectRoomType(btnLiving, 1));
        btnKitchen.setOnClickListener(v -> selectRoomType(btnKitchen, 2));
        btnBathroom.setOnClickListener(v -> selectRoomType(btnBathroom, 3));
        btnStudy.setOnClickListener(v -> selectRoomType(btnStudy, 4));
        btnBedroom.setOnClickListener(v -> selectRoomType(btnBedroom, 5));
        btnHall.setOnClickListener(v -> selectRoomType(btnHall, 6));
        btnOther.setOnClickListener(v -> selectRoomType(btnOther, 7));
        selectRoomType(btnLiving, 1);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSaveRoom).setOnClickListener(v -> saveRoom());
    }
    private void selectRoomType(MaterialButton selectedButton, int typeId) {
        resetButtonBackgrounds();
        selectedButton.setBackgroundTintList(getColorStateList(R.color.blue_selected));
        selectedTypeId = typeId;
    }
    private void resetButtonBackgrounds() {
        btnLiving.setBackgroundTintList(getColorStateList(R.color.gray_button));
        btnKitchen.setBackgroundTintList(getColorStateList(R.color.gray_button));
        btnBathroom.setBackgroundTintList(getColorStateList(R.color.gray_button));
        btnStudy.setBackgroundTintList(getColorStateList(R.color.gray_button));
        btnBedroom.setBackgroundTintList(getColorStateList(R.color.gray_button));
        btnHall.setBackgroundTintList(getColorStateList(R.color.gray_button));
        btnOther.setBackgroundTintList(getColorStateList(R.color.gray_button));
    }
    private void saveRoom() {
        String roomName = roomNameEdit.getText().toString().trim();
        if (roomName.isEmpty()) { Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show(); return; }
        if (selectedTypeId == -1) { Toast.makeText(this, "Выберите тип", Toast.LENGTH_SHORT).show(); return; }
        roomName = roomName.substring(0,1).toUpperCase() + roomName.substring(1).toLowerCase();
        try {
            JSONObject room = new JSONObject();
            room.put("user_id", userId);
            room.put("name_room", roomName);
            room.put("type_id", selectedTypeId);
            supabaseClient.addRoom(room, new SupabaseClient.SupabaseCallback() {
                @Override public void onSuccess(int code, String resp) {
                    runOnUiThread(() -> {
                        if (code == 201) { Toast.makeText(AddRoomActivity.this, "Комната добавлена", Toast.LENGTH_SHORT).show(); finish(); }
                        else Toast.makeText(AddRoomActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                    });
                }
                @Override public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(AddRoomActivity.this, "Ошибка: "+error, Toast.LENGTH_SHORT).show());
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }
}