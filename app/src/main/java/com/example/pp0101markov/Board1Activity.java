package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Board1Activity extends AppCompatActivity {

    private Button buttonStart;
    private TextView textSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_1);

        buttonStart = findViewById(R.id.buttonStart);
        textSkip = findViewById(R.id.textSkip);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Board1Activity.this, Board2Activity.class);
                startActivity(intent);
                finish();
            }
        });

        textSkip.setOnClickListener(v -> {
            AuthDialog authDialog = new AuthDialog(Board1Activity.this);
            authDialog.show();
        });
    }
}
