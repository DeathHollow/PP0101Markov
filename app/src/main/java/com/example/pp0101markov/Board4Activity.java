package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pp0101markov.adapters.MasterAdapter;
import com.example.pp0101markov.models.Master;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Board4Activity extends AppCompatActivity {
    private SupabaseClient supabaseClient;
    private ListView mastersListView;
    private String selectedCategory;
    ImageView PrevBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_4);
        PrevBtn=findViewById(R.id.previousBtn);

        supabaseClient = new SupabaseClient();
        mastersListView = findViewById(R.id.listViewMasters);

        selectedCategory = getIntent().getStringExtra("selected_category");
        fetchMastersByCategory(selectedCategory);
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
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Master>>() {}.getType();
        List<Master> masters = gson.fromJson(responseBody, listType);
        ArrayAdapter<Master> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, masters);
        mastersListView.setAdapter(adapter);
    }
}
