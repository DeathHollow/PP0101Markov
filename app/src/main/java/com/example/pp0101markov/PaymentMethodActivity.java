package com.example.pp0101markov;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentMethodActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_CARD = 200;
    private LinearLayout applePayLayout, paypalLayout, cashLayout, cardLayout;
    private TextView tvCardNumber;
    private String cardNumberMasked = "", cardholderName = "", expDate = "", cvv = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        applePayLayout = findViewById(R.id.applePayLayout);
        paypalLayout = findViewById(R.id.paypalLayout);
        cashLayout = findViewById(R.id.cashLayout);
        cardLayout = findViewById(R.id.cardLayout);
        tvCardNumber = findViewById(R.id.tvCardNumber);
        Intent intent = getIntent();
        if (intent != null) {
            cardNumberMasked = intent.getStringExtra("card_number_masked");
            cardholderName = intent.getStringExtra("cardholder_name");
            expDate = intent.getStringExtra("exp_date");
            cvv = intent.getStringExtra("cvv");
        }
        updateCardUI();

        applePayLayout.setOnClickListener(v -> selectMethod("Apple Pay"));
        paypalLayout.setOnClickListener(v -> selectMethod("Paypal"));
        cashLayout.setOnClickListener(v -> selectMethod("Cash"));
        cardLayout.setOnClickListener(v -> {
            if (cardNumberMasked == null || cardNumberMasked.isEmpty()) {
                Intent addCard = new Intent(this, AddCardActivity.class);
                startActivityForResult(addCard, REQUEST_ADD_CARD);
            } else {
                Intent result = new Intent();
                result.putExtra("payment_method", "Card");
                result.putExtra("card_number_masked", cardNumberMasked);
                result.putExtra("cardholder_name", cardholderName);
                result.putExtra("exp_date", expDate);
                result.putExtra("cvv", cvv);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });
    }

    private void updateCardUI() {
        if (cardNumberMasked != null && !cardNumberMasked.isEmpty()) {
            tvCardNumber.setText(cardNumberMasked);
        } else {
            tvCardNumber.setText(R.string.card);
        }
    }

    private void selectMethod(String method) {
        Intent result = new Intent();
        result.putExtra("payment_method", method);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_CARD && resultCode == Activity.RESULT_OK && data != null) {
            cardNumberMasked = data.getStringExtra("card_number_masked");
            cardholderName = data.getStringExtra("cardholder_name");
            expDate = data.getStringExtra("exp_date");
            cvv = data.getStringExtra("cvv");
            // Вернуть результат назад сразу же
            Intent result = new Intent();
            result.putExtra("payment_method", "Card");
            result.putExtra("card_number_masked", cardNumberMasked);
            result.putExtra("cardholder_name", cardholderName);
            result.putExtra("exp_date", expDate);
            result.putExtra("cvv", cvv);
            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }
}