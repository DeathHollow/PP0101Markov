package com.example.pp0101markov;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.pp0101markov.models.Profile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL = 102;

    private ImageView avatarImageView;
    private Button changeAvatarBtn;
    private EditText nameEditText, emailEditText, passwordEditText;
    private Button saveProfileBtn;

    private Uri avatarUri = null;
    private String bearertoken=DataBinding.getBearerToken();

    private Profile currentProfile;

    private SupabaseClient supabaseClient;
    private String userId = DataBinding.getUuidUser(); // Получите ID текущего пользователя из auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
       SupabaseClient supabaseClient=new SupabaseClient();
        avatarImageView = findViewById(R.id.avatarImageView);
        changeAvatarBtn = findViewById(R.id.changeAvatarButton);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        saveProfileBtn = findViewById(R.id.saveProfileButton);

        loadProfile();

        changeAvatarBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_READ_EXTERNAL);
            } else {
                openGallery();
            }
        });

        saveProfileBtn.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        supabaseClient.getProfile(userId, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(String responseBody) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Profile>>(){}.getType();
                List<Profile> profiles = gson.fromJson(responseBody, listType);
                if (profiles != null && !profiles.isEmpty()) {
                    currentProfile = profiles.get(0);
                    runOnUiThread(() -> {
                        nameEditText.setText(currentProfile.getName());
                        emailEditText.setText(currentProfile.getEmail());
                        String avatarUrl = currentProfile.getAvatar_url();
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(ProfileActivity.this)
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.master_mcmiller)
                                    .error(R.drawable.error)
                                    .circleCrop()
                                    .into(avatarImageView);
                        }
                    });
                }
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied to read storage", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            avatarUri = data.getData();
            if (avatarUri != null) {
                avatarImageView.setImageURI(avatarUri);
            }
        }
    }

    private void saveProfile() {
        String newName = nameEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString();

        if (newName.isEmpty()) {
            nameEditText.setError("Name required");
            return;
        }
        if (newEmail.isEmpty()) {
            emailEditText.setError("Email required");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            emailEditText.setError("Invalid email");
            return;
        }

        supabaseClient.updateProfile(userId, newName, newEmail,null,bearertoken, new SupabaseClient.SBC_Callback() {
            @Override
            public void onResponse(String json) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    passwordEditText.setText("");
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show());
            }
        });
        if (!newPassword.isEmpty()) {
            supabaseClient.updatePassword(userId, newPassword, new SupabaseClient.SBC_Callback() {
                @Override
                public void onResponse(String json) {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Password updated", Toast.LENGTH_SHORT).show());
                    passwordEditText.setText("");
                }

                @Override
                public void onFailure(IOException e) {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show());
                }
            });
        }
    }
}
