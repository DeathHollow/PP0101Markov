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

        // Инициализация токенов из SharedPreferences
        DataBinding.init(getApplicationContext());

        new Handler().postDelayed(this::navigate, 1500);
    }

    private void navigate() {
        String accessToken = DataBinding.getBearerToken();
        String refreshToken = DataBinding.getRefreshToken();
        String userId = DataBinding.getUuidUser();

        if (accessToken == null || accessToken.isEmpty()) {
            // Нет access_token — показываем онбординг
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        SupabaseClient supabaseClient = new SupabaseClient();
        supabaseClient.setContext(getApplicationContext());

        supabaseClient.getProfile(userId, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
                // Попробуем обновить access_token через refresh_token
                if (refreshToken != null && !refreshToken.isEmpty()) {
                    supabaseClient.refreshToken(refreshToken, new SupabaseClient.SBC_Callback() {
                        @Override
                        public void onFailure(java.io.IOException e) {
                            // refresh не удался, кидаем на LoginActivity
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
                            // refresh удался — новый access_token сохранён, пробуем снова
                            runOnUiThread(() -> {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            });
                        }
                    });
                } else {
                    // Нет refresh_token — на LoginActivity
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
                // access_token валиден — на главный экран
                runOnUiThread(() -> {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                });
            }
        });
    }
}