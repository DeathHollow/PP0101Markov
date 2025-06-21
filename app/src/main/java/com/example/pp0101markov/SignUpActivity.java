package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout userNameLayout, emailLayout, passwordLayout;
    private TextInputEditText userNameInput, emailInput, passwordInput;
    private ImageView prevBtn;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        userNameLayout = findViewById(R.id.userNameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        prevBtn=findViewById(R.id.previousBtn);

        userNameInput = findViewById(R.id.userNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        signupButton = findViewById(R.id.signupButton);

        signupButton.setOnClickListener(v -> attemptRegister());
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void attemptRegister() {
        String name = userNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        boolean valid = true;

        if (name.isEmpty()) {
            userNameLayout.setError(getString(R.string.enter_your_name));
            valid = false;
        } else {
            userNameLayout.setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError(getString(R.string.enter_a_valid_email));
            valid = false;
        } else {
            emailLayout.setError(null);
        }
        if (!password.matches("^[a-zA-Z0-9]+$")) {
            passwordLayout.setError(getString(R.string.the_password_must_contain_only_latin_letters_and_numbers));
            valid = false;
        } else if (password.length() > 8) {
            passwordLayout.setError(getString(R.string.password_must_be_less_then_8_characters));
            valid = false;
        } else if (password.isEmpty()) {
            passwordLayout.setError(getString(R.string.required_field));
            valid = false;
        } else {
            passwordLayout.setError(null);
        }

        if (!valid) return;

        SupabaseClient client = new SupabaseClient();
        client.setContext(this);
        signupButton.setEnabled(false);
        client.registerUser(name, email, password, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
                runOnUiThread(() -> {
                    signupButton.setEnabled(true);
                    Toast.makeText(SignUpActivity.this, getString(R.string.registration_failed) + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    signupButton.setEnabled(true);
                    Toast.makeText(SignUpActivity.this, R.string.registration_successful, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignUpActivity.this, PinCodeActivity.class));
                    finish();
                });
            }
        });
    }
}