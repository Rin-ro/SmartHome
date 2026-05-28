package com.example.smarthome.api;

import android.content.Context;
import android.content.SharedPreferences;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Уберите /rest/v1/ в конце!
    private static final String BASE_URL = "https://mukanzegpteswadvptut.supabase.co/";
    private static final String API_KEY = "sb_publishable_4iCq_7_wFxhIJYmiL8z9Ug_6RxDT7R4";

    private static Retrofit retrofit = null;
    private static String authToken = null;

    public static void setAuthToken(String token) {
        authToken = token;
    }

    public static void loadTokenFromPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("smart_home_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        if (token != null) setAuthToken(token);
    }

    public static SupabaseApi getApi() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        okhttp3.Request original = chain.request();
                        okhttp3.Request.Builder builder = original.newBuilder()
                                .header("apikey", API_KEY)
                                .header("Content-Type", "application/json");
                        if (authToken != null) {
                            builder.header("Authorization", "Bearer " + authToken);
                        }
                        return chain.proceed(builder.build());
                    })
                    .addInterceptor(logging)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(SupabaseApi.class);
    }
}