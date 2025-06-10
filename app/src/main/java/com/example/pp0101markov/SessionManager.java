package com.example.pp0101markov;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PIN = "pin_code";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public static String authToken = null;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String email, String password, String token) {
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
        authToken = token;
    }

    public void savePinCode(String pin) {
        editor.putString(KEY_PIN, pin);
        editor.apply();
    }

    public String getPinCode() {
        return prefs.getString(KEY_PIN, null);
    }

    public boolean isLoggedIn() {
        return prefs.contains(KEY_EMAIL) && prefs.contains(KEY_PASSWORD);
    }

    public void logout() {
        editor.clear();
        editor.apply();
        authToken = null;
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getPassword() {
        return prefs.getString(KEY_PASSWORD, null);
    }
}