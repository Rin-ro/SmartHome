package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private TextView addressTextView;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("smart_home_prefs", MODE_PRIVATE);

        addressTextView = findViewById(R.id.addressTextView);
        LinearLayout roomKitchen = findViewById(R.id.roomKitchen);
        LinearLayout roomBathroom = findViewById(R.id.roomBathroom);
        ImageView btnSettings = findViewById(R.id.btnSettings);
        FloatingActionButton fabAddRoom = findViewById(R.id.fabAddRoom);

        roomKitchen.setOnClickListener(v -> openRoom("Кухня"));
        roomBathroom.setOnClickListener(v -> openRoom("Ванная"));

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

        fabAddRoom.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddRoomActivity.class)));

        updateAddressDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAddressDisplay();
    }

    private void updateAddressDisplay() {
        String address = prefs.getString("address", "г. Омск, ул. Ленина, д. 24");
        if (addressTextView != null) {
            addressTextView.setText(address);
        }
    }

    private void openRoom(String roomName) {
        Intent intent = new Intent(MainActivity.this, DevicesActivity.class);
        intent.putExtra("room_name", roomName);
        startActivity(intent);
    }
}