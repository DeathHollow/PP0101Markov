package com.example.pp0101markov;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SupabaseClient {

    public interface SBC_Callback {
        public void onFailure(IOException e);
        public void onResponse(String responseBody);
    }

    public static String DOMAIN_NAME = "https://cicljuulqucdsfkqygib.supabase.co/";
    public static String REST_PATH = "rest/v1/";
    public static String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNpY2xqdXVscXVjZHNma3F5Z2liIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDcxMTE5NTIsImV4cCI6MjA2MjY4Nzk1Mn0.jBzZ8OO5LgtalG8P_f-pe3gLI9VV0Qzt07PSFQSZo5M";

    OkHttpClient client = new OkHttpClient();

    public void fetchCurrentUser(final SBC_Callback callback) {
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "profiles?select=*&id=eq." + DataBinding.getUuidUser())
                .get()
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    callback.onResponse(responseBody);
                } else {
                    callback.onFailure(new IOException("Ошибка сервера: " + response));
                }
            }
        });
    }

    public void fetchAllUserOrders (final SBC_Callback callback) {
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "orders?select=*,category(*)&id_profile=eq." + DataBinding.getUuidUser())
                .get()
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    callback.onResponse(responseBody);
                } else {
                    callback.onFailure(new IOException("Ошибка сервера: " + response));
                }
            }
        });
    }

    public void fetchAllOrders (final SBC_Callback callback) {
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "orders?select=*,category(*)")
                .get()
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    callback.onResponse(responseBody);
                } else {
                    callback.onFailure(new IOException("Ошибка сервера: " + response));
                }
            }
        });
    }
}
