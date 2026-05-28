package com.example.smarthome.api;

import com.example.smarthome.Device;
import com.example.smarthome.Room;
import com.example.smarthome.models.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface SupabaseApi {
    // Auth
    @POST("auth/v1/signup")
    Call<AuthResponse> signup(@Body SignUpRequest request);

    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> signin(@Body SignInRequest request);

    // Profile
    @GET("rest/v1/profiles")
    Call<List<Profile>> getProfile(@Query("id") String userId);

    @POST("rest/v1/profiles")
    Call<Void> insertProfile(@Body Profile profile);

    // Удалите или закомментируйте старый метод upsertProfile (или оставьте, но не используйте)
    @PATCH("rest/v1/profiles")
    Call<Void> updateProfile(@Query("id") String userId, @Body Profile profile);

    // Rooms
    @GET("rest/v1/rooms")
    Call<List<Room>> getRooms(@Query("user_id") String userId);

    @POST("rest/v1/rooms")
    Call<Room> addRoom(@Body Room room);

    @DELETE("rest/v1/rooms")
    Call<Void> deleteRoom(@Query("id") int id);

    // Devices
    @GET("rest/v1/devices")
    Call<List<Device>> getDevices(@Query("room_id") int roomId);

    @POST("rest/v1/devices")
    Call<Device> addDevice(@Body Device device);

    @PATCH("rest/v1/devices")
    Call<Void> updateDevice(@Query("id") int id, @Body Device device);
}