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

    private Uri selectedAvatarUri = null;
    private String uploadedAvatarUrl = null;

    private SupabaseClient supabaseClient;

    private String userId;
    private String bearerToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        avatarImageView = findViewById(R.id.avatarImageView);
        changeAvatarButton = findViewById(R.id.changeAvatarButton);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        supabaseClient = new SupabaseClient();
        supabaseClient.setContext(this);
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
                runOnUiThread(() ->
                        Toast.makeText(EditProfileActivity.this, R.string.profile_upload_error, Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    try {
                        JSONArray array = new JSONArray(responseBody);
                        if (array.length() > 0) {
                            JSONObject profile = array.getJSONObject(0);
                            nameEditText.setText(profile.optString("full_name"));
                            uploadedAvatarUrl = profile.optString("avatar_url");
                            Glide.with(EditProfileActivity.this)
                                    .load("https://cicljuulqucdsfkqygib.supabase.co/storage/v1/object/avatars/" + uploadedAvatarUrl)
                                    .placeholder(R.drawable.master_mcmiller)
                                    .into(avatarImageView);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(EditProfileActivity.this, R.string.profile_upload_error, Toast.LENGTH_SHORT).show();
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

        if (newName.isEmpty() && newEmail.isEmpty()) {
            Toast.makeText(this, R.string.name_and_email_are_required, Toast.LENGTH_SHORT).show();
            return;
        }
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
                    runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, R.string.profile_upload_error, Toast.LENGTH_SHORT).show());
                }
            });
        } else {
            updateProfileOnSupabase(newName, newEmail, newPassword, uploadedAvatarUrl);
        }
    }

    private void updateProfileOnSupabase(String name, String email, String password, String avatarUrl) {
        supabaseClient.updateProfile(userId,name, null, avatarUrl,bearerToken, new SupabaseClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                if ((!email.isEmpty() && !email.equals(DataBinding.getUuidUser())) &&  !password.isEmpty()) {
                    supabaseClient.updateAuthUser(bearerToken, email, password, new SupabaseClient.SBC_Callback() {
                        @Override
                        public void onResponse(String responseBody) {
                            runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, R.string.profile_email_and_or_password_updated, Toast.LENGTH_SHORT).show());
                            finish();
                        }
                        @Override
                        public void onFailure(IOException e) {
                            runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, R.string.email_password_update_error, Toast.LENGTH_SHORT).show());
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, R.string.the_profile_has_been_updated, Toast.LENGTH_SHORT).show());
                    finish();
                }
            }
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, R.string.profile_update_error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
