package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        DataBinding.init(getApplicationContext());

        new Handler().postDelayed(this::navigate, 1500);
    }

    private void navigate() {
        String accessToken = DataBinding.getBearerToken();
        String refreshToken = DataBinding.getRefreshToken();
        String userId = DataBinding.getUuidUser();

        if (accessToken == null || accessToken.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        SupabaseClient supabaseClient = new SupabaseClient();
        supabaseClient.setContext(getApplicationContext());

        supabaseClient.getProfile(userId, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
                if (refreshToken != null && !refreshToken.isEmpty()) {
                    supabaseClient.refreshToken(refreshToken, new SupabaseClient.SBC_Callback() {
                        @Override
                        public void onFailure(java.io.IOException e) {
                            runOnUiThread(() -> {
                                DataBinding.saveBearerToken(getApplicationContext(), null);
                                DataBinding.saveRefreshToken(getApplicationContext(), null);
                                DataBinding.saveUuidUser(getApplicationContext(), null);
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                finish();
                            });
                        }

                        @Override
                        public void onResponse(String responseBody) {
                            runOnUiThread(() -> {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            });
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        DataBinding.saveBearerToken(getApplicationContext(), null);
                        DataBinding.saveRefreshToken(getApplicationContext(), null);
                        DataBinding.saveUuidUser(getApplicationContext(), null);
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    });
                }
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                });
            }
        });
    }
}