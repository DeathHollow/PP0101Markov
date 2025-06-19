package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Board2Activity extends AppCompatActivity {

    private ImageView layoutNail, layoutEyebrows, layoutMassage, layoutHair;
    private TextView textSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_2);
        layoutNail = findViewById(R.id.imageNail);
        layoutEyebrows = findViewById(R.id.imageEyebrows);
        layoutMassage = findViewById(R.id.imageMassage);
        layoutHair = findViewById(R.id.imageHair);
        textSkip = findViewById(R.id.textSkip);

        View.OnClickListener categoryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category = "";
                int id = view.getId();
                if (id == R.id.imageNail) {
                    category = "1";
                } else if (id == R.id.imageEyebrows) {
                    category = "4";
                } else if (id == R.id.imageMassage) {
                    category = "3";
                } else if (id == R.id.imageHair) {
                    category = "2";
                }
                Intent intent = new Intent(Board2Activity.this, Board3Activity.class);
                intent.putExtra("selected_category", category);
                startActivity(intent);
            }
        };

        layoutNail.setOnClickListener(categoryClickListener);
        layoutEyebrows.setOnClickListener(categoryClickListener);
        layoutMassage.setOnClickListener(categoryClickListener);
        layoutHair.setOnClickListener(categoryClickListener);

        textSkip.setOnClickListener(v -> {
            AuthDialog authDialog = new AuthDialog(Board2Activity.this);
            authDialog.show();
        });
    }
}
