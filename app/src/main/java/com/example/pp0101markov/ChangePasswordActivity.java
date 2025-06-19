package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputLayout passwordLayout, confirmLayout;
    private TextInputEditText passwordInput, confirmInput;
    private Button confirmButton;

    private SupabaseClient supabaseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        passwordLayout = findViewById(R.id.userNameLayout);
        passwordInput = findViewById(R.id.userNameInput);
        confirmLayout = findViewById(R.id.emailLayout);
        confirmInput = findViewById(R.id.emailInput);
        confirmButton = findViewById(R.id.signupButton);

        supabaseClient = new SupabaseClient();

        confirmButton.setOnClickListener(v -> tryChangePassword());
    }

    private void tryChangePassword() {
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";
        String confirm = confirmInput.getText() != null ? confirmInput.getText().toString().trim() : "";

        if (password.isEmpty()) {
            passwordLayout.setError("Enter the password");
            return;
        } else {
            passwordLayout.setError(null);
        }
        if (confirm.isEmpty()) {
            confirmLayout.setError("Confirm the password");
            return;
        } else {
            confirmLayout.setError(null);
        }
        if (!password.equals(confirm)) {
            confirmLayout.setError("Passwords don't match");
            return;
        } else {
            confirmLayout.setError(null);
        }
        if (!password.matches("^[a-zA-Z0-9]+$")) {
            passwordLayout.setError("The password must contain only Latin letters and numbers.");
            return;
        }
        if (password.length() > 8) {
            passwordLayout.setError("The password must not be longer than 8 characters.");
            return;
        } else {
            passwordLayout.setError(null);
        }

        confirmButton.setEnabled(false);

        String bearerToken = DataBinding.getBearerToken();
        if (bearerToken == null || bearerToken.isEmpty()) {
            confirmButton.setEnabled(true);
            Toast.makeText(this, "Error: authorization token not received", Toast.LENGTH_LONG).show();
            return;
        }

        supabaseClient.updatePassword(bearerToken, password, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
                runOnUiThread(() -> {
                    confirmButton.setEnabled(true);
                    Toast.makeText(ChangePasswordActivity.this, "Password change error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    confirmButton.setEnabled(true);
                    Toast.makeText(ChangePasswordActivity.this, "The password has been successfully changed!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                    finish();
                });
            }
        });
    }
}
