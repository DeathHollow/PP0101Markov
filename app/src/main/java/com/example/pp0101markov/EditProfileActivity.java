package com.example.pp0101markov;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private ImageView avatarImageView;
    private Button changeAvatarButton, saveProfileButton;
    private EditText nameEditText, emailEditText, passwordEditText;

    private Uri selectedAvatarUri = null; // URI выбранного аватара из галереи
    private String uploadedAvatarUrl = null; // URL загруженного аватара в Supabase Storage

    private SupabaseClient supabaseClient; // Класс для работы с Supabase (описан ниже)

    private String userId;       // UUID пользователя
    private String bearerToken;  // JWT токен авторизации

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile); // ваш xml

        avatarImageView = findViewById(R.id.avatarImageView);
        changeAvatarButton = findViewById(R.id.changeAvatarButton);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        supabaseClient = new SupabaseClient();

        // Получите userId и bearerToken из сессии/хранилища
        userId = DataBinding.getUuidUser();
        bearerToken = DataBinding.getBearerToken();

        loadUserProfile();

        changeAvatarButton.setOnClickListener(v -> openGallery());

        saveProfileButton.setOnClickListener(v -> saveProfile());
    }

    private void loadUserProfile() {
        supabaseClient.getProfile(userId, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                Toast.makeText(EditProfileActivity.this, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    try {
                        // Парсим JSON массива, берем первый элемент
                        JSONArray array = new JSONArray(responseBody);
                        if (array.length() > 0) {
                            JSONObject profile = array.getJSONObject(0);
                            nameEditText.setText(profile.optString("full_name"));
                            emailEditText.setText(profile.optString("email"));
                            uploadedAvatarUrl = profile.optString("avatar_url");

                            // Загрузить аватар (например, через Glide)
                            Glide.with(EditProfileActivity.this)
                                    .load("https://cicljuulqucdsfkqygib.supabase.co/storage/v1/object/avatars/" + uploadedAvatarUrl)
                                    .placeholder(R.drawable.master_mcmiller)
                                    .into(avatarImageView);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(EditProfileActivity.this, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedAvatarUri = data.getData();
            avatarImageView.setImageURI(selectedAvatarUri);
        }
    }

    private void saveProfile() {
        String newName = nameEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Имя и email обязательны", Toast.LENGTH_SHORT).show();
            return;
        }

        // Если выбран новый аватар — сначала загрузить в Supabase Storage
        if (selectedAvatarUri != null) {
            supabaseClient.setContext(this);
            supabaseClient.uploadAvatar(selectedAvatarUri, bearerToken, new SupabaseClient.SBC_Callback() {
                @Override
                public void onResponse(String avatarUrl) {
                    uploadedAvatarUrl = avatarUrl;
                    updateProfileOnSupabase(newName, newEmail, newPassword, avatarUrl);
                }

                @Override
                public void onFailure(IOException e) {
                    runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "Ошибка загрузки аватара", Toast.LENGTH_SHORT).show());
                }
            });
        } else {
            updateProfileOnSupabase(newName, newEmail, newPassword, uploadedAvatarUrl);
        }
    }

    private void updateProfileOnSupabase(String name, String email, String password, String avatarUrl) {
        supabaseClient.updateProfile(userId, bearerToken, name, email, avatarUrl, new SupabaseClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                if (!password.isEmpty()) {
                    supabaseClient.updatePassword(bearerToken, password, new SupabaseClient.SBC_Callback() {
                        @Override
                        public void onResponse(String responseBody) {
                            runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "Профиль и пароль обновлены", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onFailure(IOException e) {
                            runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "Ошибка обновления пароля", Toast.LENGTH_SHORT).show());
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "Профиль обновлен", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "Ошибка обновления профиля", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
