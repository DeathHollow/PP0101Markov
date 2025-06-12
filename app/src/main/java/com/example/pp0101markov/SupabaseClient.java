package com.example.pp0101markov;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseClient {

    public void updatePassword(String bearerToken, String password, SBC_Callback sbcCallback) {
    }

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
    public void getProfile(String userId, Callback callback) {
        HttpUrl url = HttpUrl.parse(DOMAIN_NAME +REST_PATH + "/rest/v1/profiles")
                .newBuilder()
                .addQueryParameter("id", "eq." + userId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResponse(response.body().string());
                } else {
                    callback.onFailure(new Exception("Response failed: " + response.code()));
                }
            }
        });
    }
    public void updateProfile(String userId, String name, String email, Callback callback) {
        String url = DOMAIN_NAME + REST_PATH + "/rest/v1/profiles?id=eq." + userId;

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String jsonBody = "{\"name\":\"" + name + "\",\"email\":\"" + email + "\"}";

        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }
            @Override public void onResponse(@NonNull Call call,@NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    callback.onResponse(responseBody);
                } else {
                    callback.onFailure(new Exception("Update failed: " + response.code()));
                }
            }
        });
    }
}
