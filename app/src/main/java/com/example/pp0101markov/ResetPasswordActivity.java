package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
    private SupabaseClient supabaseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_screen);

        emailEditText = findViewById(R.id.emailEditText);
        confirmButton = findViewById(R.id.confirmButton);
        backButton = findViewById(R.id.backButton);

        supabaseClient = new SupabaseClient();

        confirmButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter the correct email address", Toast.LENGTH_SHORT).show();
                return;
            }
            confirmButton.setEnabled(false);
            supabaseClient.recoverPassword(email, new SupabaseClient.SBC_Callback() {
                @Override
                public void onFailure(java.io.IOException e) {
                    runOnUiThread(() -> {
                        confirmButton.setEnabled(true);
                        Toast.makeText(ResetPasswordActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onResponse(String responseBody) {
                    runOnUiThread(() -> {
                        confirmButton.setEnabled(true);
                        Toast.makeText(ResetPasswordActivity.this, "A password reset email has been sent to: " + email, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, OTPActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    });
                }
            });
        });

        backButton.setOnClickListener(v -> finish());
    }
}