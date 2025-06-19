package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.pp0101markov.models.Profile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ImageView avatarImageView;
    private TextView nameTextView, emailTextView;
    private Button btnMyOrders, btnLogout, btnEditProfile;

    private SupabaseClient supabaseClient=new SupabaseClient();
    private final String userId=DataBinding.getUuidUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nameTextView=findViewById(R.id.profileName);
        avatarImageView = findViewById(R.id.profileImage);
        btnEditProfile=findViewById(R.id.changeProfileBtn);
        btnMyOrders = findViewById(R.id.btnMyOrders);
        btnLogout = findViewById(R.id.exitBtn);

        loadProfile();

        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        btnMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, BookingsActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.logout();
            DataBinding.clear(this);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadProfile() {
        supabaseClient.setContext(this);
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
                    Profile profile = profiles.get(0);
                    runOnUiThread(() -> {
                        nameTextView.setText(profile.getName());
                        String avatarUrl = profile.getAvatar_url();
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(ProfileActivity.this)
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.master_mcmiller)
                                    .circleCrop()
                                    .into(avatarImageView);
                        }
                    });
                }
            }
        });
    }
}