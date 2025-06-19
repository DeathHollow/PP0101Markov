package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout userNameLayout, emailLayout, passwordLayout;
    private TextInputEditText userNameInput, emailInput, passwordInput;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        userNameLayout = findViewById(R.id.userNameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        userNameInput = findViewById(R.id.userNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        signupButton = findViewById(R.id.signupButton);

        signupButton.setOnClickListener(v -> attemptRegister());

    }

    private void attemptRegister() {
        String name = userNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        boolean valid = true;

        if (name.isEmpty()) {
            userNameLayout.setError("Enter your name");
            valid = false;
        } else {
            userNameLayout.setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Enter a valid email");
            valid = false;
        } else {
            emailLayout.setError(null);
        }

        if (password.length() > 8) {
            passwordLayout.setError("Password must be less then 8 characters");
            valid = false;
        } else {
            passwordLayout.setError(null);
        }


        if (!valid) return;

        SupabaseClient client = new SupabaseClient();
        signupButton.setEnabled(false);
        client.registerUser(email, password, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
                runOnUiThread(() -> {
                    signupButton.setEnabled(true);
                    Toast.makeText(SignUpActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    signupButton.setEnabled(true);
                    Toast.makeText(SignUpActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignUpActivity.this, PinCodeActivity.class));
                    finish();
                });
            }
        });
    }
}