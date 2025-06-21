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
            passwordLayout.setError(getString(R.string.enter_the_password));
            return;
        } else {
            passwordLayout.setError(null);
        }
        if (confirm.isEmpty()) {
            confirmLayout.setError(getString(R.string.confirm_the_password));
            return;
        } else {
            confirmLayout.setError(null);
        }
        if (!password.equals(confirm)) {
            confirmLayout.setError(getString(R.string.passwords_don_t_match));
            return;
        } else {
            confirmLayout.setError(null);
        }
        if (!password.matches("^[a-zA-Z0-9]+$")) {
            passwordLayout.setError(getString(R.string.the_password_must_contain_only_latin_letters_and_numbers));
            return;
        }
        if (password.length() > 8) {
            passwordLayout.setError(getString(R.string.the_password_must_not_be_longer_than_8_characters));
            return;
        } else {
            passwordLayout.setError(null);
        }

        confirmButton.setEnabled(false);

        String bearerToken = DataBinding.getBearerToken();
        if (bearerToken == null || bearerToken.isEmpty()) {
            confirmButton.setEnabled(true);
            Toast.makeText(this, R.string.error_authorization_token_not_received, Toast.LENGTH_LONG).show();
            return;
        }

        supabaseClient.updatePassword(bearerToken, password, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
                runOnUiThread(() -> {
                    confirmButton.setEnabled(true);
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.password_change_error) + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    confirmButton.setEnabled(true);
                    Toast.makeText(ChangePasswordActivity.this, R.string.the_password_has_been_successfully_changed, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                    finish();
                });
            }
        });
    }
}
