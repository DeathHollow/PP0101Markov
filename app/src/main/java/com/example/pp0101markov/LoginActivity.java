package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    TextView authBtn;

    private void init() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login1_screen);
        loginBtn =findViewById(R.id.continueButton);
        init();
    }

    boolean correctEmail = false, countEmail = false, isNullEmail = false;
    boolean countPassword = false, isNullPassword = false;

    private void TextFieldsValidate() {
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String email = s.toString().trim();
                if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailLayout.setError("Введите корретнный email");
                    correctEmail = false;
                } else {
                    emailLayout.setError(null);
                    correctEmail = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > emailLayout.getCounterMaxLength()) {
                    emailLayout.setError("Электронная почта не может содержать больше "
                            + emailLayout.getCounterMaxLength() + " символов");
                    countEmail = false;
                } else {
                    countEmail = true;
                }
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = passwordEditText.getText().toString();
                if (password.length() >= 8){
                    passwordLayout.setError("Пароль не должен содержать больше 8 символов");
                    countPassword = false;
                } else {
                    passwordLayout.setError(null);
                    countPassword = true;
                }
            }
        });

        View.OnClickListener listener = v -> {
            boolean validPassword = checkPassword();
            boolean validEmail = checkEmail();
            if (validPassword && validEmail) {
                String email = emailLayout.getEditText().getText().toString();
                String password = passwordLayout.getEditText().getText().toString();
            }
        };

        loginBtn.setOnClickListener(listener);
        authBtn.setOnClickListener(listener);
    }

    private void loginUser(String email, String password) {
        startActivity(new Intent(getApplicationContext(), OrdersActivity.class));
    }

    private void authUser(String email, String password) {
        startActivity(new Intent(getApplicationContext(), OrdersActivity.class));
    }

    private boolean checkPassword() {
        if (passwordEditText.getText().toString().isEmpty()) {
            isNullPassword = false;
            passwordLayout.setError("Обязательное поле");
        } else {
            isNullPassword = true;
        }
        if (countPassword && isNullPassword) {
            return true;
        }
        return false;
    }

    private boolean checkEmail() {
        if (emailEditText.getText().toString().isEmpty()) {
            isNullEmail = false;
            emailLayout.setError("Обязательное поле");
        } else {
            isNullEmail = true;
        }
        if (correctEmail && countEmail && isNullEmail) {
            return true;
        }
        return false;
    }
}