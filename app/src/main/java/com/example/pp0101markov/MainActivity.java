package com.example.pp0101markov;

import android.os.Bundle;
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
    private RecyclerView servicesRecyclerView;
    private ServicesAdapter servicesAdapter;
    private List<Service_main> serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameTextView = findViewById(R.id.userNameTextView);
        tabLayout = findViewById(R.id.tabLayout);
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);

        userNameTextView.setText("Carol");

        setupTabs();
        setupServicesRecyclerView();
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
