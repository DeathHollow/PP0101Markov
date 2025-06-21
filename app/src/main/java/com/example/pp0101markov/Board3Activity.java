package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pp0101markov.adapters.ServiceAdapter;
import com.example.pp0101markov.models.Master;
import com.example.pp0101markov.models.Service;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Board3Activity extends AppCompatActivity {
    private SupabaseClient supabaseClient;
    private ListView servicesListView;
    private String selectedCategory;
    private ImageView prevBtn;

    private String selectedService;
    private double servicePrice=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_3);

        prevBtn = findViewById(R.id.previousBtn);
        supabaseClient = new SupabaseClient();
        servicesListView = findViewById(R.id.listViewServices);
        selectedCategory = getIntent().getStringExtra("selected_category");

        fetchServicesByCategory(selectedCategory);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void fetchServicesByCategory(String category) {
        supabaseClient.setContext(this);
        supabaseClient.getServicesByCategory(category, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String responseBody) {
                updateUIWithServices(responseBody);
            }
        });
    }

    private void updateUIWithServices(String responseBody) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Service>>() {}.getType();
        List<Service> services = gson.fromJson(responseBody, listType);

        ServiceAdapter adapter = new ServiceAdapter(this, services);
        runOnUiThread(() -> {
            servicesListView.setAdapter(adapter);
            servicesListView.setOnItemClickListener((parent, view, position, id) -> {
                Service selectedService = (Service) parent.getItemAtPosition(position);
                servicePrice = selectedService.getPrice();
                String serviceId = selectedService.getId();
                String serviceName = selectedService.getName();
                Intent intent = new Intent(Board3Activity.this, Board4Activity.class);
                intent.putExtra("service_id", serviceId);
                intent.putExtra("category_id", selectedCategory);
                intent.putExtra("service_name", serviceName);
                intent.putExtra("service_price", servicePrice);
                startActivity(intent);
            });
        });
    }
}
