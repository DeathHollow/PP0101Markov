package com.example.pp0101markov;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentMethodActivity extends AppCompatActivity {

    private LinearLayout layoutApplePay, layoutPaypal, layoutCash, layoutCard;
    private TextView tvCardNumber, tvRemoveCard;
    private static final String PREFS_NAME = "CardPrefs";
    private static final String KEY_CARD_NUMBER_MASKED = "card_number_masked";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        layoutApplePay = findViewById(R.id.applePayLayout);
        layoutPaypal = findViewById(R.id.paypalLayout);
        layoutCash = findViewById(R.id.cashLayout);
        layoutCard = findViewById(R.id.cardLayout);
        tvCardNumber = findViewById(R.id.tvCardNumber);
        tvRemoveCard = findViewById(R.id.tvRemoveCard);

        // Показать маскированный номер карты, если есть
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String cardNumber = prefs.getString(KEY_CARD_NUMBER_MASKED, null);
        if (cardNumber != null) {
            layoutCard.setVisibility(View.VISIBLE);
            tvCardNumber.setText(cardNumber);
        } else {
            layoutCard.setVisibility(View.GONE);
        }

        layoutApplePay.setOnClickListener(v -> selectPayment("Apple Pay"));
        layoutPaypal.setOnClickListener(v -> selectPayment("Paypal"));
        layoutCash.setOnClickListener(v -> selectPayment("Cash"));
        layoutCard.setOnClickListener(v -> selectPayment("Card"));

        tvRemoveCard.setOnClickListener(v -> {
            prefs.edit().remove(KEY_CARD_NUMBER_MASKED).apply();
            layoutCard.setVisibility(View.GONE);
        });
    }

    private void selectPayment(String method) {
        // Вернуть выбранный способ оплаты в предыдущую Activity
        Intent result = new Intent();
        result.putExtra("payment_method", method);
        setResult(RESULT_OK, result);
        finish();
    }
}