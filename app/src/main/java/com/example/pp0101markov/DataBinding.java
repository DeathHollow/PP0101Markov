package com.example.pp0101markov;

import android.content.Context;
import android.content.SharedPreferences;

public class DataBinding {
    private static String bearerToken;
    private static String refreshToken;
    private static String uuidUser;
    private static String pinCode;
    private static Boolean IsFirstLaunch;

    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_BEARER = "bearer_token";
    private static final String KEY_REFRESH = "refresh_token";
    private static final String KEY_USER_ID = "uuid_user";
    private static final String KEY_USER_PIN = "pin_user";
    private static final String FIRST_LAUNCH = "first_launch";


    public static void init(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        bearerToken = prefs.getString(KEY_BEARER, null);
        refreshToken = prefs.getString(KEY_REFRESH, null);
        uuidUser = prefs.getString(KEY_USER_ID, null);
        pinCode = prefs.getString(KEY_USER_PIN, null);
        IsFirstLaunch=prefs.getBoolean(FIRST_LAUNCH, true);
    }

    public static void saveBearerToken(Context context, String token) {
        bearerToken = token;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_BEARER, token).apply();
    }

    public static String getBearerToken() {
        return bearerToken;
    }
    public static void saveFirstLaunch(Context context, Boolean value) {
        IsFirstLaunch = value;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(FIRST_LAUNCH, value).apply();
    }

    public static Boolean getIsFirstLaunch() {
        return IsFirstLaunch;
    }
    public static void savePincode(Context context, String pin) {
        pinCode = pin;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_PIN, pin).apply();
    }

    public static String getPincode() {
        return pinCode;
    }

    public static void saveRefreshToken(Context context, String token) {
        refreshToken = token;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_REFRESH, token).apply();
    }

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static void saveUuidUser(Context context, String uuid) {
        uuidUser = uuid;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_ID, uuid).apply();
    }

    public static String getUuidUser() {
        return uuidUser;
    }
    public static void clear(Context context) {
        bearerToken = null;
        refreshToken = null;
        uuidUser = null;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}