package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pp0101markov.models.OrdersActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout emailLayout, passwordLayout;
    TextInputEditText emailEditText, passwordEditText;
    Button loginBtn;
    TextView authBtn, forgotPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login1_screen);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailEditText = findViewById(R.id.emailInput);
        passwordEditText = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.continueButton);
        authBtn = findViewById(R.id.authBtn); // регистрация
        forgotPasswordBtn = findViewById(R.id.forgotPasswordButton); // восстановление

        TextFieldsValidate();

        loginBtn.setOnClickListener(v -> tryLogin());
        if (authBtn != null) {
            authBtn.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            });
        }
        if (forgotPasswordBtn != null) {
            forgotPasswordBtn.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            });
        }
    }

    private void TextFieldsValidate() {
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String email = s.toString().trim();
                if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailLayout.setError("Введите корректный email");
                } else {
                    emailLayout.setError(null);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable s) {
                String password = passwordEditText.getText().toString();
                if (!password.matches("^[a-zA-Z0-9]+$")) {
                    passwordLayout.setError("Пароль должен содержать только латинские буквы и цифры");
                }
                if (password.length() > 8) {
                    passwordLayout.setError("Пароль не должен быть длиннее 8 символов");
                } else {
                    passwordLayout.setError(null);
                }
            }
        });
    }

    private boolean validateFields() {
        boolean valid = true;
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty()) {
            emailLayout.setError("Обязательное поле");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Неверный email");
            valid = false;
        } else {
            emailLayout.setError(null);
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Обязательное поле");
            valid = false;
        } else if (password.length() < 8) {
            passwordLayout.setError("Пароль должен быть не менее 8 символов");
            valid = false;
        } else {
            passwordLayout.setError(null);
        }
        return valid;
    }

    private void tryLogin() {
        if (!validateFields()) return;

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        loginBtn.setEnabled(false);
        loginBtn.setText("Вход...");

        SupabaseClient supabaseClient = new SupabaseClient();
        supabaseClient.loginUser(email, password, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
                runOnUiThread(() -> {
                    loginBtn.setEnabled(true);
                    loginBtn.setText("Войти");
                    Toast.makeText(LoginActivity.this, "Ошибка входа: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    loginBtn.setEnabled(true);
                    loginBtn.setText("Войти");
                    Toast.makeText(LoginActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, PinCodeActivity.class));
                    finish();
                });
            }
        });
    }

}