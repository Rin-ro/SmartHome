package com.example.smarthome;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SupabaseClient {
    // Замените на свои данные
    private static final String SUPABASE_URL = "https://mukanzegpteswadvptut.supabase.co";
    private static final String SUPABASE_API_KEY = "sb_publishable_4iCq_7_wFxhIJYmiL8z9Ug_6RxDT7R4";

    private static SupabaseClient instance;
    private final ExecutorService executorService;
    private final Map<String, String> baseHeaders;

    private SupabaseClient() {
        executorService = Executors.newCachedThreadPool();
        baseHeaders = new HashMap<>();
        baseHeaders.put("apikey", SUPABASE_API_KEY);
        baseHeaders.put("Authorization", "Bearer " + SUPABASE_API_KEY);
        baseHeaders.put("Content-Type", "application/json");
    }

    public static synchronized SupabaseClient getInstance() {
        if (instance == null) instance = new SupabaseClient();
        return instance;
    }

    private void executeRequest(String method, String urlString, JSONObject body, SupabaseCallback callback) {
        executorService.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(method);
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                for (Map.Entry<String, String> header : baseHeaders.entrySet())
                    conn.setRequestProperty(header.getKey(), header.getValue());
                if (body != null && (method.equals("POST") || method.equals("PUT") || method.equals("PATCH"))) {
                    conn.setDoOutput(true);
                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(body.toString().getBytes(StandardCharsets.UTF_8));
                    }
                }
                int responseCode = conn.getResponseCode();
                InputStream inputStream = (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream();
                String response = readStream(inputStream);
                if (callback != null) callback.onSuccess(responseCode, response);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            } finally {
                if (conn != null) conn.disconnect();
            }
        });
    }

    private String readStream(InputStream stream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    // ---------- Users ----------
    public void addUser(JSONObject userData, SupabaseCallback callback) {
        executeRequest("POST", SUPABASE_URL + "/rest/v1/users", userData, callback);
    }
    public void loginUser(String email, String password, SupabaseCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/users?email=eq." + email + "&password=eq." + password;
        executeRequest("GET", url, null, callback);
    }
    public void updateUserAddress(String userId, String address, SupabaseCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("address", address);
            executeRequest("PATCH", SUPABASE_URL + "/rest/v1/users?user_id=eq." + userId, data, callback);
        } catch (Exception e) { callback.onError(e.getMessage()); }
    }
    public void updateUserProfile(String userId, JSONObject userData, SupabaseCallback callback) {
        executeRequest("PATCH", SUPABASE_URL + "/rest/v1/users?user_id=eq." + userId, userData, callback);
    }

    // ---------- Rooms ----------
    public void getRoomsWithType(String userId, SupabaseCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/rooms?select=room_id,name_room,type_id,room_types(name_type,image_type)&user_id=eq." + userId;
        executeRequest("GET", url, null, callback);
    }
    // Добавление нового типа комнаты
    public void addRoomType(JSONObject roomTypeData, SupabaseCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/room_types";
        executeRequest("POST", url, roomTypeData, callback);
    }

    // getRoomTypes уже есть, но он должен возвращать все типы (стандартные + пользовательские)
// Ваш текущий getRoomTypes делает это через or=(user_id.is.null,user_id.eq.userId) – это правильно.
    public void getRoomTypes(String userId, SupabaseCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/room_types?select=*&or=(user_id.is.null,user_id.eq." + userId + ")";
        executeRequest("GET", url, null, callback);
    }
    public void addRoom(JSONObject roomData, SupabaseCallback callback) {
        executeRequest("POST", SUPABASE_URL + "/rest/v1/rooms", roomData, callback);
    }

    // ---------- Devices ----------
    public void getDevicesByRoomIdWithType(String roomId, SupabaseCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/device?select=*,device_types(device_name_type,device_image_type)&room_id=eq." + roomId;
        executeRequest("GET", url, null, callback);
    }
    public void getDeviceById(String deviceId, SupabaseCallback callback) {
        executeRequest("GET", SUPABASE_URL + "/rest/v1/device?device_id=eq." + deviceId, null, callback);
    }
    public void updateDeviceWorkStatus(String deviceId, boolean workStatus, SupabaseCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("work", workStatus);
            executeRequest("PATCH", SUPABASE_URL + "/rest/v1/device?device_id=eq." + deviceId, data, callback);
        } catch (Exception e) { callback.onError(e.getMessage()); }
    }
    public void updateDeviceParameters(String deviceId, String parametersJson, SupabaseCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("parameters", parametersJson);
            executeRequest("PATCH", SUPABASE_URL + "/rest/v1/device?device_id=eq." + deviceId, data, callback);
        } catch (Exception e) { callback.onError(e.getMessage()); }
    }
    public void addDevice(JSONObject deviceData, SupabaseCallback callback) {
        executeRequest("POST", SUPABASE_URL + "/rest/v1/device", deviceData, callback);
    }

    public interface SupabaseCallback {
        void onSuccess(int responseCode, String response);
        void onError(String error);
    }
}