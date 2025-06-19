package com.example.pp0101markov;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddCardActivity extends AppCompatActivity {

    private EditText etCardholderName, etCardNumber, etExpDate, etCVV;
    private Button btnAddCard;

    private static final String PREFS_NAME = "CardPrefs";
    private static final String KEY_CARDHOLDER_NAME = "cardholder_name";
    private static final String KEY_CARD_NUMBER_MASKED = "card_number_masked";
    private static final String KEY_EXP_DATE = "exp_date";
    private static final String KEY_CVV = "cvv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcard_activity);
        etCardholderName = findViewById(R.id.cardholder_name);
        etCardNumber = findViewById(R.id.card_number);
        etExpDate = findViewById(R.id.exp_date);
        etCVV = findViewById(R.id.cvv);
        btnAddCard = findViewById(R.id.btn_add_card);

        btnAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCard();
            }
        });
    }

    private void addCard() {
        String name = etCardholderName.getText().toString().trim();
        String cardNumber = etCardNumber.getText().toString().trim().replaceAll("\\s+", "");
        String expDate = etExpDate.getText().toString().trim();
        String cvv = etCVV.getText().toString().trim();

        // Валидация
        if (TextUtils.isEmpty(name)) {
            etCardholderName.setError("Введите имя владельца карты");
            etCardholderName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cardNumber) || cardNumber.length() < 8) {
            etCardNumber.setError("Введите корректный номер карты");
            etCardNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(expDate)) {
            etExpDate.setError("Введите срок действия");
            etExpDate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cvv) || cvv.length() < 3) {
            etCVV.setError("Введите CVV");
            etCVV.requestFocus();
            return;
        }

        String maskedCardNumber = maskCardNumber(cardNumber);

        saveCardData(name, maskedCardNumber, expDate, cvv);

        Toast.makeText(this, "Карта успешно добавлена", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() < 8) {
            return cardNumber.replaceAll(".", "x");
        }
        String first4 = cardNumber.substring(0, 4);
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        StringBuilder maskedMiddle = new StringBuilder();
        for (int i = 0; i < cardNumber.length() - 8; i++) {
            maskedMiddle.append("x");
        }
        return first4 + maskedMiddle.toString() + last4;
    }

    private void saveCardData(String name, String maskedCardNumber, String expDate, String cvv) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(KEY_CARDHOLDER_NAME, name);
        editor.putString(KEY_CARD_NUMBER_MASKED, maskedCardNumber);
        editor.putString(KEY_EXP_DATE, expDate);
        editor.putString(KEY_CVV, cvv);
        editor.apply();
    }

    private void clearFields() {
        etCardholderName.setText("");
        etCardNumber.setText("");
        etExpDate.setText("");
        etCVV.setText("");
    }
}
