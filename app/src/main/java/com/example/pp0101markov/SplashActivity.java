package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        DataBinding.init(getApplicationContext());

        new Handler().postDelayed(this::navigate, 1500);
    }

    private void navigate() {
        DataBinding.init(getApplicationContext());
        if (DataBinding.getIsFirstLaunch()) {
            startActivity(new Intent(this, Board1Activity.class));
            finish();
            return;
        }

        String accessToken = DataBinding.getBearerToken();
        String userId = DataBinding.getUuidUser();
        String pin = DataBinding.getPincode();
        if (accessToken == null || accessToken.isEmpty() || userId == null || userId.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        if (pin == null || pin.isEmpty()) {
            startActivity(new Intent(this, PinCodeActivity.class));
            finish();
            return;
        }
        startActivity(new Intent(this, PinCodeActivity.class));
        finish();
    }
}