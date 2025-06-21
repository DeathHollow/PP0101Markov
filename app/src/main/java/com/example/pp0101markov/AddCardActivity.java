package com.example.pp0101markov;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddCardActivity extends AppCompatActivity {
    private EditText etCardholderName, etCardNumber, etExpDate, etCVV;
    private Button btnAddCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcard_activity);

        etCardholderName = findViewById(R.id.cardholder_name);
        etCardNumber = findViewById(R.id.card_number);
        etExpDate = findViewById(R.id.exp_date);
        etCVV = findViewById(R.id.cvv);
        btnAddCard = findViewById(R.id.btn_add_card);

        btnAddCard.setOnClickListener(view -> {
            String name = etCardholderName.getText().toString().trim();
            String cardNumber = etCardNumber.getText().toString().trim();
            String expDate = etExpDate.getText().toString().trim();
            String cvv = etCVV.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(cardNumber) ||
            TextUtils.isEmpty(expDate) || TextUtils.isEmpty(cvv)) {
                Toast.makeText(this, R.string.fill_in_all_the_fields, Toast.LENGTH_SHORT).show();
                return;
            }
            String masked = maskCardNumber(cardNumber);
            Intent result = new Intent();
            result.putExtra("cardholder_name", name);
            result.putExtra("card_number_masked", masked);
            result.putExtra("exp_date", expDate);
            result.putExtra("cvv", cvv);
            setResult(Activity.RESULT_OK, result);
            finish();
        });
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() < 8) return "xxxx xxxx";
        String first4 = cardNumber.substring(0, 4);
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return first4 + " xxxx xxxx " + last4;
    }
}