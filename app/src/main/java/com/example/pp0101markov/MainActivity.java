package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pp0101markov.adapters.ServicesAdapter;
import com.example.pp0101markov.models.Service_main;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView userNameTextView;
    private TabLayout tabLayout;
    private ImageView menuButton;
    private RecyclerView servicesRecyclerView;
    private ServicesAdapter servicesAdapter;
    private List<Service_main> serviceList;
    SupabaseClient supabaseClient = new SupabaseClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supabaseClient.setContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadUserNameFromProfile();
        userNameTextView = findViewById(R.id.userNameTextView);
        tabLayout = findViewById(R.id.tabLayout);
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
        menuButton=findViewById(R.id.menuButton);
        userNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SalonDetailActivity.class);
                startActivity(intent);
            }
        });


        setupTabs();
        setupServicesRecyclerView();
    }
    private void loadUserNameFromProfile() {
        String userId = DataBinding.getUuidUser();
        if (userId == null || userId.isEmpty()) {
            userNameTextView.setText("User");
            return;
        }
        if (supabaseClient == null) supabaseClient = new SupabaseClient();
        supabaseClient.getProfile(userId, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
                runOnUiThread(() -> userNameTextView.setText("User"));
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    try {
                        // Используем org.json для разбора (или можно Gson)
                        org.json.JSONArray array = new org.json.JSONArray(responseBody);
                        if (array.length() > 0) {
                            org.json.JSONObject profile = array.getJSONObject(0);
                            String fullName = profile.optString("full_name", "User");
                            userNameTextView.setText(fullName);
                        } else {
                            userNameTextView.setText("User");
                        }
                    } catch (Exception e) {
                        userNameTextView.setText("User");
                    }
                });
            }
        });
    }
    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Recommended"));
        tabLayout.addTab(tabLayout.newTab().setText("Packages"));
        tabLayout.addTab(tabLayout.newTab().setText("Professionals"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(MainActivity.this, "Selected: " + tab.getText(), Toast.LENGTH_SHORT).show();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupServicesRecyclerView() {
        serviceList = new ArrayList<>();
        serviceList.add(new Service_main("Haircut", "45 mins", "$90", R.drawable.hair_service));
        serviceList.add(new Service_main("Massage", "60 mins", "$60", R.drawable.massage));
        servicesAdapter = new ServicesAdapter(this, serviceList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        servicesRecyclerView.setLayoutManager(layoutManager);
        servicesRecyclerView.setAdapter(servicesAdapter);
    }
}
