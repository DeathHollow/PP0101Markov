package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OTPActivity extends AppCompatActivity {

    private EditText[] otpDigits = new EditText[6];
    private Button btnNext;
    private SupabaseClient supabaseClient;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_screen);

        otpDigits[0] = findViewById(R.id.pinDigit1);
        otpDigits[1] = findViewById(R.id.pinDigit2);
        otpDigits[2] = findViewById(R.id.pinDigit3);
        otpDigits[3] = findViewById(R.id.pinDigit4);
        otpDigits[4] = findViewById(R.id.pinDigit5);
        otpDigits[5] = findViewById(R.id.pinDigit6);

        btnNext = findViewById(R.id.btnLogout);
        btnNext.setText("Send code");

        supabaseClient = new SupabaseClient();
        email = getIntent().getStringExtra("email");

        for (int i = 0; i < otpDigits.length; i++) {
            final int index = i;
            otpDigits[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < otpDigits.length - 1) {
                        otpDigits[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        otpDigits[index - 1].requestFocus();
                    }
                }
            });
        }

        btnNext.setOnClickListener(v -> checkOtp());
    }

    private void checkOtp() {
        StringBuilder enteredOtp = new StringBuilder();
        for (EditText et : otpDigits) {
            enteredOtp.append(et.getText().toString());
        }

        if (enteredOtp.length() < 6) {
            Toast.makeText(this, "Enter the 6-digit code", Toast.LENGTH_SHORT).show();
            return;
        }

        btnNext.setEnabled(false);

        supabaseClient.verifyOtp(email, enteredOtp.toString(), "recovery", new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
                runOnUiThread(() -> {
                    btnNext.setEnabled(true);
                    Toast.makeText(OTPActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    btnNext.setEnabled(true);
                    Toast.makeText(OTPActivity.this, "The code is confirmed!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OTPActivity.this, ChangePasswordActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("otp", enteredOtp.toString());
                    startActivity(intent);
                    finish();
                });
            }
        });
    }
}