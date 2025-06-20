package com.example.pp0101markov;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
public class SupabaseClient {
    public static final String DOMAIN = "https://cicljuulqucdsfkqygib.supabase.co/";
    public static final String REST = "rest/v1/", AUTH = "auth/v1/", API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNpY2xqdXVscXVjZHNma3F5Z2liIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDcxMTE5NTIsImV4cCI6MjA2MjY4Nzk1Mn0.jBzZ8OO5LgtalG8P_f-pe3gLI9VV0Qzt07PSFQSZo5M";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private android.content.Context context;


    public void setContext(android.content.Context ctx) { this.context = ctx; }
    public android.content.Context getContext() { return this.context; }

    public interface SBC_Callback {
        void onFailure(IOException e);
        void onResponse(String responseBody);
    }

    private Request.Builder baseReq(String url) {
        return new Request.Builder().url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json");
    }
    private RequestBody jsonBody(Object obj) {
        return RequestBody.create(gson.toJson(obj), MediaType.parse("application/json; charset=utf-8"));
    }
    private void handleResponse(Response resp, SBC_Callback cb, java.util.function.Consumer<String> onSuccess) {
        try (ResponseBody b = resp.body()) {
            String s = b != null ? b.string() : "";
            if (resp.isSuccessful()) onSuccess.accept(s);
            else cb.onFailure(new IOException(s));
        } catch (IOException e) { cb.onFailure(e); }
    }

    // --- Регистрация ---
    public void registerUser(String name, String email, String password, SBC_Callback cb) {
        JsonObject j = new JsonObject();
        j.addProperty("full_name", name);
        j.addProperty("email", email);
        j.addProperty("password", password);
        client.newCall(baseReq(DOMAIN + AUTH + "signup").post(jsonBody(j)).build()).enqueue(new Callback() {
            public void onFailure(@NonNull Call c, @NonNull IOException e) { cb.onFailure(e); }
            public void onResponse(@NonNull Call c, @NonNull Response r) {
                handleResponse(r, cb, s -> {
                    try {
                        JsonObject resp = gson.fromJson(s, JsonObject.class);
                        String token = resp.has("access_token") ? resp.get("access_token").getAsString() : null;
                        String refresh = resp.has("refresh_token") ? resp.get("refresh_token").getAsString() : null;
                        String id = resp.has("user") && resp.getAsJsonObject("user").has("id") ? resp.getAsJsonObject("user").get("id").getAsString() : null;
                        if (context != null) {
                            if (token != null) DataBinding.saveBearerToken(context, token);
                            if (refresh != null) DataBinding.saveRefreshToken(context, refresh);
                            if (id != null) DataBinding.saveUuidUser(context, id);
                        }
                    } catch (Exception ignored) {}
                    cb.onResponse(s);
                });
            }
        });
    }
    public void loginUser(String email, String password, SBC_Callback cb) {
        JsonObject j = new JsonObject();
        j.addProperty("email", email);
        j.addProperty("password", password);
        client.newCall(baseReq(DOMAIN + AUTH + "token?grant_type=password").post(jsonBody(j)).build()).enqueue(new Callback() {
            public void onFailure(@NonNull Call c, @NonNull IOException e) { cb.onFailure(e); }
            public void onResponse(@NonNull Call c, @NonNull Response r) {
                handleResponse(r, cb, s -> {
                    try {
                        JsonObject resp = gson.fromJson(s, JsonObject.class);
                        String token = resp.has("access_token") ? resp.get("access_token").getAsString() : null;
                        String refresh = resp.has("refresh_token") ? resp.get("refresh_token").getAsString() : null;
                        String id = resp.has("user") && resp.getAsJsonObject("user").has("id") ? resp.getAsJsonObject("user").get("id").getAsString() : null;
                        if (context != null) {
                            if (token != null) DataBinding.saveBearerToken(context, token);
                            if (refresh != null) DataBinding.saveRefreshToken(context, refresh);
                            if (id != null) DataBinding.saveUuidUser(context, id);
                        }
                    } catch (Exception ignored) {}
                    cb.onResponse(s);
                });
            }
        });
    }

    // --- Пример запроса с bearer token ---
    public void getProfile(String userId, SBC_Callback cb) {
        String token = context != null ? DataBinding.getBearerToken() : null;
        String url = DOMAIN + REST + "profiles?id=eq." + userId + "&select=id,full_name,avatar_url,email";
        Request req = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) { cb.onFailure(e); }
            @Override public void onResponse(@NonNull Call call, @NonNull Response resp) {
                try (ResponseBody body = resp.body()) {
                    String str = body != null ? body.string() : "";
                    if (resp.isSuccessful()) cb.onResponse(str);
                    else cb.onFailure(new IOException("Profile load failed: " + str));
                } catch (IOException e) { cb.onFailure(e); }
            }
        });
    }
    public void updateProfile(String userId, String name, String email, String avatarUrl, String bearerToken, SBC_Callback cb) {
        JsonObject j = new JsonObject();
        j.addProperty("full_name", name);
        j.addProperty("email", email);
        if (avatarUrl != null) j.addProperty("avatar_url", avatarUrl);
        client.newCall(baseReq(DOMAIN + REST + "profiles?id=eq." + userId)
                        .patch(jsonBody(j)).addHeader("Authorization", "Bearer " + bearerToken).build())
                .enqueue(new Callback() {
                    public void onFailure(@NonNull Call c, @NonNull IOException e) { cb.onFailure(e); }
                    public void onResponse(@NonNull Call c, @NonNull Response r) { handleResponse(r, cb, cb::onResponse); }
                });
    }
    public void updateAuthUser(String bearerToken, String newEmail, String newPassword, SBC_Callback cb) {
        JsonObject j = new JsonObject();
        j.addProperty("email", newEmail);
        j.addProperty("password", newPassword);
        client.newCall(baseReq(DOMAIN + AUTH + "user")
                        .put(jsonBody(j))
                        .addHeader("Authorization", "Bearer " + bearerToken).build())
                .enqueue(new okhttp3.Callback() {
                    public void onFailure(@NonNull okhttp3.Call c, @NonNull java.io.IOException e) { cb.onFailure(e); }
                    public void onResponse(@NonNull okhttp3.Call c, @NonNull okhttp3.Response r) { handleResponse(r, cb, cb::onResponse); }
                });
    }
    // UPDATE PASSWORD
    public void updatePassword(String bearerToken, String password, SBC_Callback cb) {
        JsonObject j = new JsonObject();
        j.addProperty("password", password);
        client.newCall(baseReq(DOMAIN + AUTH + "user").put(jsonBody(j)).addHeader("Authorization", "Bearer " + bearerToken).build())
                .enqueue(new Callback() {
                    public void onFailure(@NonNull Call c, @NonNull IOException e) { cb.onFailure(e); }
                    public void onResponse(@NonNull Call c, @NonNull Response r) { handleResponse(r, cb, cb::onResponse); }
                });
    }
    public void uploadAvatar(Uri selectedAvatarUri, String bearerToken, SBC_Callback cb) {
        String fileName = System.currentTimeMillis() + ".jpg";
        String url = DOMAIN + "storage/v1/object/avatars/" + fileName;
        try {
            java.io.InputStream inputStream = getContext().getContentResolver().openInputStream(selectedAvatarUri);
            if (inputStream == null) {
                cb.onFailure(new IOException("Cannot open image from Uri"));
                return;
            }
            byte[] imageBytes = readAllBytes(inputStream);

            RequestBody requestBody = RequestBody.create(imageBytes, MediaType.parse("image/jpeg"));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", API_KEY)
                    .addHeader("Authorization", "Bearer " + bearerToken)
                    .addHeader("Content-Type", "image/jpeg")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                public void onFailure(@NonNull Call c, @NonNull IOException e) { cb.onFailure(e); }
                public void onResponse(@NonNull Call c, @NonNull Response r) {
                    if (r.isSuccessful()) {
                        cb.onResponse(fileName);
                    } else {
                        try (ResponseBody body = r.body()) {
                            String str = body != null ? body.string() : "";
                            cb.onFailure(new IOException("Upload failed: " + str));
                        } catch (IOException e) {
                            cb.onFailure(e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            cb.onFailure(new IOException("Upload failed: " + e.getMessage(), e));
        }
    }
    private byte[] readAllBytes(java.io.InputStream inputStream) throws IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        inputStream.close();
        return buffer.toByteArray();
    }
    public void recoverPassword(String email, SBC_Callback cb) {
        JsonObject j = new JsonObject();
        j.addProperty("email", email);
        j.addProperty("type", "recovery");
        client.newCall(baseReq(DOMAIN + AUTH + "otp")
                        .post(jsonBody(j)).build())
                .enqueue(new Callback() {
                    public void onFailure(@NonNull Call c, @NonNull IOException e) { cb.onFailure(e); }
                    public void onResponse(@NonNull Call c, @NonNull Response r) {
                        handleResponse(r, cb, cb::onResponse);
                    }
                });
    }
    public void verifyOtp(String email, String token, String type, SBC_Callback cb) {
        JsonObject j = new JsonObject();
        j.addProperty("email", email);
        j.addProperty("token", token);
        j.addProperty("type", type);
        client.newCall(baseReq(DOMAIN + AUTH + "verify")
                        .post(jsonBody(j)).build())
                .enqueue(new Callback() {
                    public void onFailure(@NonNull Call c, @NonNull IOException e) { cb.onFailure(e); }
                    public void onResponse(@NonNull Call c, @NonNull Response r) {
                        handleResponse(r, cb, cb::onResponse);
                    }
                });
    }
    public void getMasters(SBC_Callback cb) {
        String url = DOMAIN + REST + "masters?select=id,name,service_category,avatar_url"; // Убедитесь, что у вас есть правильный путь к таблице мастеров
        Request req = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + DataBinding.getBearerToken())
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response resp) {
                handleResponse(resp, cb, cb::onResponse);
            }
        });
    }
    public void checkEmailExists(String email, SBC_Callback cb) {
        String url = DOMAIN + REST + "profiles?email=eq." + email + "&select=id";
        Request req = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) { cb.onFailure(e); }
            @Override public void onResponse(@NonNull Call call, @NonNull Response resp) {
                try (ResponseBody body = resp.body()) {
                    String str = body != null ? body.string() : "";
                    if (resp.isSuccessful()) cb.onResponse(str);
                    else cb.onFailure(new IOException(str));
                } catch (IOException e) { cb.onFailure(e); }
            }
        });
    }
    public void getServices(SBC_Callback cb) {
        String url = DOMAIN + REST + "services?select=id,name,price"; // Убедитесь, что у вас есть правильный путь к таблице сервисов
        Request req = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + DataBinding.getBearerToken())
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response resp) {
                handleResponse(resp, cb, cb::onResponse);
            }
        });
    }
    public void getBookings(String profileId, SBC_Callback cb) {
        // Query 'orders' table, filtered by id_profile
        String url = DOMAIN + REST + "orders?id_profile=eq." + profileId +
                "&select=id,name,date,id_category,id_profile,id_professional,id_service,price";
        Request req = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + DataBinding.getBearerToken())
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response resp) {
                handleResponse(resp, cb, cb::onResponse);
            }
        });
    }
    public void refreshToken(String refreshToken, SBC_Callback cb) {
        JsonObject j = new JsonObject();
        j.addProperty("refresh_token", refreshToken);
        client.newCall(baseReq(DOMAIN + AUTH + "token?grant_type=refresh_token")
                .post(jsonBody(j)).build()).enqueue(new Callback() {
            public void onFailure(@NonNull Call c, @NonNull IOException e) { cb.onFailure(e); }
            public void onResponse(@NonNull Call c, @NonNull Response r) {
                handleResponse(r, cb, s -> {
                    try {
                        JsonObject resp = gson.fromJson(s, JsonObject.class);
                        String newAccess = resp.has("access_token") ? resp.get("access_token").getAsString() : null;
                        String newRefresh = resp.has("refresh_token") ? resp.get("refresh_token").getAsString() : null;
                        String id = resp.has("user") && resp.getAsJsonObject("user").has("id") ? resp.getAsJsonObject("user").get("id").getAsString() : null;
                        if (context != null) {
                            if (newAccess != null) DataBinding.saveBearerToken(context, newAccess);
                            if (newRefresh != null) DataBinding.saveRefreshToken(context, newRefresh);
                            if (id != null) DataBinding.saveUuidUser(context, id);
                        }
                    } catch (Exception ignored) {}
                    cb.onResponse(s);
                });
            }
        });
    }
    public void getServicesByCategory(String category, SBC_Callback cb) {
        String url = DOMAIN + REST + "services?category_id=eq." + category + "&select=*";
        Request req = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response resp) {
                handleResponse(resp, cb, cb::onResponse);
            }
        });
    }
    public void getMastersByCategory(String category, SBC_Callback cb) {
        String url = DOMAIN + REST + "masters?category_id=eq." + category + "&select=*";
        Request req = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response resp) {
                handleResponse(resp, cb, cb::onResponse);
            }
        });
    }
    public void cancelBooking(String bookingId, SBC_Callback cb) {
        String url = DOMAIN + REST + "bookings?id=eq." + bookingId;

        client.newCall(baseReq(url)
                .delete() // Send a DELETE request
                .addHeader("Authorization", "Bearer " + DataBinding.getBearerToken())
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response resp) {
                handleResponse(resp, cb, cb::onResponse);
            }
        });
    }
    public void createBooking(String userId, String day, String time, String master, SBC_Callback cb) {
        Map<String, Object> booking = new HashMap<>();
        booking.put("user_id", userId);
        booking.put("day", day);
        booking.put("time", time);
        booking.put("master", master);

        String url = DOMAIN + REST + "bookings";

        client.newCall(baseReq(url)
                .post(jsonBody(booking))
                .addHeader("Authorization", "Bearer " + DataBinding.getBearerToken())
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response resp) {
                handleResponse(resp, cb, cb::onResponse);
            }
        });
    }
}
