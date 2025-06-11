package com.example.pp0101markov;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button confirmButton;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_screen);

        emailEditText = findViewById(R.id.emailEditText);
        confirmButton = findViewById(R.id.confirmButton);
        backButton = findViewById(R.id.backButton);

        confirmButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            // TODO: Add logic to send reset password email
            Toast.makeText(this, "Reset password email sent to: " + email, Toast.LENGTH_SHORT).show();
        });

        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}