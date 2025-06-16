package com.example.pp0101markov;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
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

    public interface SBC_Callback {
        void onFailure(IOException e);
        void onResponse(String responseBody);
    }

    // UTILS
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

    // REGISTRATION
    public void registerUser(String email, String password, SBC_Callback cb) {
        JsonObject j = new JsonObject();
        j.addProperty("email", email);
        j.addProperty("password", password);
        client.newCall(baseReq(DOMAIN + AUTH + "signup").post(jsonBody(j)).build()).enqueue(new Callback() {
            public void onFailure(@NonNull Call c, @NonNull IOException e) { cb.onFailure(e); }
            public void onResponse(@NonNull Call c, @NonNull Response r) {
                handleResponse(r, cb, s -> {
                    try {
                        JsonObject resp = gson.fromJson(s, JsonObject.class);
                        String token = resp.has("access_token") ? resp.get("access_token").getAsString() : null;
                        String id = resp.has("id") ? resp.get("id").getAsString() : null;
                        if (token != null && !token.isEmpty()) DataBinding.saveBearerToken(token);
                        if (id != null && !id.isEmpty()) DataBinding.saveUuidUser(id);
                    } catch (Exception ignored) {}
                    cb.onResponse(s);
                });
            }
        });
    }

    // LOGIN
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
                        String id = resp.has("id") ? resp.get("id").getAsString() : null;
                        if (token != null && !token.isEmpty()) DataBinding.saveBearerToken(token);
                        if (id != null && !id.isEmpty()) DataBinding.saveUuidUser(id);
                    } catch (Exception ignored) {}
                    cb.onResponse(s);
                });
            }
        });
    }
    // GET PROFILE
    public void getProfile(String userId, SBC_Callback cb) {
        String url = DOMAIN + REST + "profiles?id=eq." + userId + "&select=id,full_name,avatar_url,email";
        Request req = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + DataBinding.getBearerToken())
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

    // UPDATE PROFILE
    public void updateProfile(String userId, String name, String email, String avatarUrl, String bearerToken, SBC_Callback cb) {
        JsonObject j = new JsonObject();
        j.addProperty("name", name);
        j.addProperty("email", email);
        if (avatarUrl != null) j.addProperty("avatar_url", avatarUrl);
        client.newCall(baseReq(DOMAIN + REST + "profiles?id=eq." + userId)
                        .patch(jsonBody(j)).addHeader("Authorization", "Bearer " + bearerToken).build())
                .enqueue(new Callback() {
                    public void onFailure(@NonNull Call c, @NonNull IOException e) { cb.onFailure(e); }
                    public void onResponse(@NonNull Call c, @NonNull Response r) { handleResponse(r, cb, cb::onResponse); }
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
        client.newCall(baseReq(DOMAIN + AUTH + "recover")
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
    private android.content.Context context;
    public void setContext(android.content.Context ctx) { this.context = ctx; }
    public android.content.Context getContext() { return this.context; }
}
