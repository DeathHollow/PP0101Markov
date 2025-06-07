package com.example.pp0101markov;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Board3Activity extends AppCompatActivity {

    ListView listView;
    ServiceAdapter adapter;
    List<Service> services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_3);

        listView = findViewById(R.id.listViewServices);

        services = new ArrayList<>();
        services.add(new Service(R.drawable.basic_manicure, "Basic Manicure", "$30"));
        services.add(new Service(R.drawable.basic_pedicure, "Basic Pedicure", "$35"));
        services.add(new Service(R.drawable.gel_manicure, "Gel Manicure", "$50"));
        services.add(new Service(R.drawable.gel_pedicure, "Gel Pedicure", "$55"));
        services.add(new Service(R.drawable.acrylic_extensions, "Acrylic Extensions", "$100"));

        adapter = new ServiceAdapter(this, services);
        listView.setAdapter(adapter);
    }
}