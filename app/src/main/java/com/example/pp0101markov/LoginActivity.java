package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
        authBtn = findViewById(R.id.authBtn);
        forgotPasswordBtn = findViewById(R.id.forgotPasswordButton);

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
                    emailLayout.setError("Enter the correct email address");
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
                    passwordLayout.setError("The password must contain only Latin letters and numbers.");
                }
                if (password.length() > 8) {
                    passwordLayout.setError("The password must not be longer than 8 characters.");
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
            emailLayout.setError("Required field");
            valid = false;
        } else {
            emailLayout.setError(null);
        }
        if (password.isEmpty()) {
            passwordLayout.setError("Required field");
            valid = false;
        } else if (password.length() > 8) {
            passwordLayout.setError("The password must be no longer than 8 characters.");
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
        loginBtn.setText("Entrance...");
        SupabaseClient supabaseClient = new SupabaseClient();
        supabaseClient.setContext(this);
        supabaseClient.loginUser(email, password, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
                runOnUiThread(() -> {
                    loginBtn.setEnabled(true);
                    loginBtn.setText(R.string.enter);
                    Toast.makeText(LoginActivity.this, "Login error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    loginBtn.setEnabled(true);
                    loginBtn.setText(R.string.enter);
                    Toast.makeText(LoginActivity.this, "Successful login", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, PinCodeActivity.class));
                    finish();
                });
            }
        });
    }

}