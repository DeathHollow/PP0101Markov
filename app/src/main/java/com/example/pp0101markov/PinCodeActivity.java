
package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PinCodeActivity extends AppCompatActivity {

    private EditText[] pinDigits = new EditText[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pincode_activity);

        pinDigits[0] = findViewById(R.id.pinDigit1);
        pinDigits[1] = findViewById(R.id.pinDigit2);
        pinDigits[2] = findViewById(R.id.pinDigit3);
        pinDigits[3] = findViewById(R.id.pinDigit4);
        pinDigits[4] = findViewById(R.id.pinDigit5);

        for (int i = 0; i < pinDigits.length; i++) {
            final int index = i;
            pinDigits[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1) {
                        if (index < pinDigits.length - 1) {
                            pinDigits[index + 1].requestFocus();
                        } else {
                            checkPin();
                        }
                    } else if (s.length() == 0 && index > 0) {
                        pinDigits[index - 1].requestFocus();
                    }
                }
            });
        }

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            // Очищаем пин и токены при выходе (если нужно)
            DataBinding.savePincode(getApplicationContext(), null);
            DataBinding.saveBearerToken(getApplicationContext(), null);
            DataBinding.saveUuidUser(getApplicationContext(), null);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void checkPin() {
        StringBuilder enteredPin = new StringBuilder();
        for (EditText et : pinDigits) {
            enteredPin.append(et.getText().toString());
        }

        String savedPin = DataBinding.getPincode();
        if (savedPin == null) {
            // Первый раз — сохраняем
            DataBinding.savePincode(getApplicationContext(), enteredPin.toString());
            Toast.makeText(this, "The PIN code is set", Toast.LENGTH_SHORT).show();
            if (DataBinding.getIsFirstLaunch()) {
                startActivity(new Intent(this, Board1Activity.class));
                finish();
            }
            else {
                openMainScreen();
            }
        } else {
            if (savedPin.equals(enteredPin.toString())) {
                if (DataBinding.getIsFirstLaunch()) {
                    startActivity(new Intent(this, Board1Activity.class));
                    finish();
                }
                else {
                    openMainScreen();
                }
            } else {
                Toast.makeText(this, "Invalid PIN code", Toast.LENGTH_SHORT).show();
                clearPinFields();
            }
        }
    }

    private void clearPinFields() {
        for (EditText et : pinDigits) {
            et.setText("");
        }
        pinDigits[0].requestFocus();
    }

    private void openMainScreen() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}