package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smarthome.api.ApiClient;
import com.example.smarthome.api.SupabaseApi;
import com.example.smarthome.models.Room;
import com.google.android.material.button.MaterialButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRoomActivity extends AppCompatActivity {
    private EditText editRoomName;
    private MaterialButton btnLiving, btnKitchen, btnBathroom, btnOther;
    private String selectedType = "Гостиная";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);
        editRoomName = findViewById(R.id.editRoomName);
        btnLiving = findViewById(R.id.btnLiving);
        btnKitchen = findViewById(R.id.btnKitchen);
        btnBathroom = findViewById(R.id.btnBathroom);
        btnOther = findViewById(R.id.btnOther);
        MaterialButton btnSave = findViewById(R.id.btnSaveRoom);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnLiving.setOnClickListener(v -> setSelected(btnLiving, "Гостиная"));
        btnKitchen.setOnClickListener(v -> setSelected(btnKitchen, "Кухня"));
        btnBathroom.setOnClickListener(v -> setSelected(btnBathroom, "Ванная"));
        btnOther.setOnClickListener(v -> startActivityForResult(new Intent(this, AddCategoryActivity.class), 1));

        btnSave.setOnClickListener(v -> {
            String name = editRoomName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show();
                return;
            }
            name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
            saveRoom(name, selectedType);
        });
    }

    private void setSelected(MaterialButton btn, String type) {
        resetButtons();
        btn.setBackgroundTintList(getColorStateList(R.color.blue_selected));
        selectedType = type;
    }

    private void resetButtons() {
        int def = getColor(R.color.gray_button);
        btnLiving.setBackgroundTintList(getColorStateList(def));
        btnKitchen.setBackgroundTintList(getColorStateList(def));
        btnBathroom.setBackgroundTintList(getColorStateList(def));
        if (btnOther != null) btnOther.setBackgroundTintList(getColorStateList(def));
    }

    private void saveRoom(String name, String type) {
        String userId = prefs.getString("user_id", "");
        Room room = new Room();
        room.user_id = userId;
        room.name = name;
        room.type = type;
        SupabaseApi api = ApiClient.getApi();
        api.addRoom(room).enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if (response.isSuccessful()) {
                    finish();
                } else {
                    showError("Ошибка добавления комнаты");
                }
            }
            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                showError("Нет соединения");
            }
        });
    }

    private void showError(String msg) {
        new AlertDialog.Builder(this).setTitle("Ошибка").setMessage(msg)
                .setPositiveButton("OK", null).setCancelable(false).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String newCat = data.getStringExtra("category");
            if (newCat != null) {
                // можно обновить список выбора, но для простоты просто устанавливаем её как выбранную
                selectedType = newCat;
                Toast.makeText(this, "Выбрано: " + newCat, Toast.LENGTH_SHORT).show();
            }
        }
    }
}