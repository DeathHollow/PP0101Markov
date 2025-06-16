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
            passwordLayout.setError("Введите пароль");
            return;
        } else {
            passwordLayout.setError(null);
        }
        if (confirm.isEmpty()) {
            confirmLayout.setError("Подтвердите пароль");
            return;
        } else {
            confirmLayout.setError(null);
        }
        if (!password.equals(confirm)) {
            confirmLayout.setError("Пароли не совпадают");
            return;
        } else {
            confirmLayout.setError(null);
        }
        if (!password.matches("^[a-zA-Z0-9]+$")) {
            passwordLayout.setError("Пароль должен содержать только латинские буквы и цифры");
            return;
        }
        if (password.length() > 8) {
            passwordLayout.setError("Пароль не должен быть длиннее 8 символов");
            return;
        } else {
            passwordLayout.setError(null);
        }

        confirmButton.setEnabled(false);

        String bearerToken = DataBinding.getBearerToken();
        if (bearerToken == null || bearerToken.isEmpty()) {
            confirmButton.setEnabled(true);
            Toast.makeText(this, "Ошибка: не получен токен авторизации", Toast.LENGTH_LONG).show();
            return;
        }

        supabaseClient.updatePassword(bearerToken, password, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
                runOnUiThread(() -> {
                    confirmButton.setEnabled(true);
                    Toast.makeText(ChangePasswordActivity.this, "Ошибка смены пароля: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    confirmButton.setEnabled(true);
                    Toast.makeText(ChangePasswordActivity.this, "Пароль успешно изменён!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                    finish();
                });
            }
        });
    }
}
