package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pp0101markov.adapters.MasterAdapter;
import com.example.pp0101markov.models.Master;
import com.example.pp0101markov.models.Service;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Board4Activity extends AppCompatActivity {
    private SupabaseClient supabaseClient;
    private ListView mastersListView;
    private String selectedCategory, selectedId, selectedServiceName, selectedMasterName, Avatar;
    private Double selectedPrice;
    private ImageView imgProfile;

    ImageView prevBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_4);
        prevBtn=findViewById(R.id.previousBtn);
        supabaseClient = new SupabaseClient();
        mastersListView = findViewById(R.id.listViewMasters);
        selectedId = getIntent().getStringExtra("service_id");
        selectedServiceName=getIntent().getStringExtra("service_name");
        selectedCategory = getIntent().getStringExtra("category_id");
        selectedPrice = getIntent().getDoubleExtra("service_price", 0);
        fetchMastersByCategory(selectedCategory);
        mastersListView.setOnItemClickListener((adapterView, view, position, id) -> {
            Master selectedMaster = (Master) adapterView.getItemAtPosition(position);
            selectedMasterName = selectedMaster.getName();
            Intent intent = new Intent(Board4Activity.this, MasterBookingActivity.class);
            intent.putExtra("service_id", selectedId);
            intent.putExtra("avatar_url", selectedMaster.getAvatar_url());
            intent.putExtra("service_name", selectedServiceName);
            intent.putExtra("service_price", selectedPrice);
            intent.putExtra("master_name", selectedMasterName);
            intent.putExtra("master_category", selectedMaster.getCategory_id());
            intent.putExtra("master_reviews", selectedMaster.getReviews());
            intent.putExtra("master_id", selectedMaster.getId());
            startActivity(intent);
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void fetchMastersByCategory(String category) {
        supabaseClient.getMastersByCategory(category, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String responseBody) {
                updateUIWithMasters(responseBody);
            }
        });
    }
    private void updateUIWithMasters(String responseBody) {
        runOnUiThread(() -> {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Master>>() {
            }.getType();
            List<Master> masters = gson.fromJson(responseBody, listType);
            MasterAdapter adapter = new MasterAdapter(this, masters);
            mastersListView.setAdapter(adapter);
        });
    }
}
