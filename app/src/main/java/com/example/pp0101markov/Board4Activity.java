package com.example.pp0101markov;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Board4Activity extends AppCompatActivity {

    ListView listView;
    MasterAdapter adapter;
    List<Master> masters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_4);

        listView = findViewById(R.id.listViewMasters);

        masters = new ArrayList<>();
        masters.add(new Master(R.drawable.master_mcmiller, "Jordan Mcmiller", "Nail Designer", 4.9));

        adapter = new MasterAdapter(this, masters);
        listView.setAdapter(adapter);
    }
}