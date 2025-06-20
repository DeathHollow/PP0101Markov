package com.example.pp0101markov;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CheckOutActivity extends AppCompatActivity {
    private static final int REQUEST_PAYMENT_METHOD = 101;
    private TextView textDate, textTime, textService, textLocation, textCardNumber, textTotal;
    private ImageView cardIcon;
    private Button buttonBook;
    private String paymentMethod = "Card";
    private String maskedCardNumber = "•••• 2345";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        textDate = findViewById(R.id.textDate);
        textTime = findViewById(R.id.textTime);
        textService = findViewById(R.id.textService);
        textLocation = findViewById(R.id.textLocation);
        textCardNumber = findViewById(R.id.textCardNumber);
        textTotal = findViewById(R.id.textTotal);
        cardIcon = findViewById(R.id.cardIcon);
        buttonBook = findViewById(R.id.buttonBook);

        // Получаем данные из предыдущего экрана
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String service = intent.getStringExtra("service");
        String location = intent.getStringExtra("location");
        String total = intent.getStringExtra("total");
        // paymentMethod - если передан, можно тоже получить

        if (!TextUtils.isEmpty(date)) textDate.setText(date);
        if (!TextUtils.isEmpty(time)) textTime.setText(time);
        if (!TextUtils.isEmpty(service)) textService.setText(service);
        if (!TextUtils.isEmpty(location)) textLocation.setText(location);
        if (!TextUtils.isEmpty(total)) textTotal.setText(total);

        // Делаем Location кликабельным для открытия карты
        textLocation.setMovementMethod(LinkMovementMethod.getInstance());
        textLocation.setOnClickListener(v -> {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW);
            mapIntent.setData(android.net.Uri.parse("geo:0,0?q=" + location));
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        });

        // Восстанавливаем маскированный номер карты (если есть)
        SharedPreferences prefs = getSharedPreferences("CardPrefs", MODE_PRIVATE);
        String cardNumber = prefs.getString("card_number_masked", null);
        if (!TextUtils.isEmpty(cardNumber)) {
            maskedCardNumber = cardNumber.length() > 4 ? "•••• " + cardNumber.substring(cardNumber.length() - 4) : cardNumber;
        }
        textCardNumber.setText(maskedCardNumber);

        // По клику по payment выбираем способ оплаты
        View paymentRow = (View) textCardNumber.getParent();
        paymentRow.setOnClickListener(v -> {
            Intent pmIntent = new Intent(CheckOutActivity.this, PaymentMethodActivity.class);
            startActivityForResult(pmIntent, REQUEST_PAYMENT_METHOD);
        });

        buttonBack.setOnClickListener(v -> finish());

        buttonBook.setOnClickListener(v -> {
            // Здесь логика бронирования (отправка на сервер/отображение успешного сообщения)
            Toast.makeText(this, "Booking confirmed!", Toast.LENGTH_SHORT).show();
            // Можно перейти на экран подтверждения или на главную
            Intent confirmIntent = new Intent(CheckOutActivity.this, ConfirmBookingActivity.class);
            confirmIntent.putExtra("day", date);
            confirmIntent.putExtra("time", time);
            confirmIntent.putExtra("address", location);
            startActivity(confirmIntent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PAYMENT_METHOD && resultCode == Activity.RESULT_OK && data != null) {
            paymentMethod = data.getStringExtra("payment_method");
            // В зависимости от способа оплаты меняем отображение
            if ("Card".equals(paymentMethod)) {
                SharedPreferences prefs = getSharedPreferences("CardPrefs", MODE_PRIVATE);
                String cardNumber = prefs.getString("card_number_masked", null);
                if (!TextUtils.isEmpty(cardNumber)) {
                    maskedCardNumber = cardNumber.length() > 4 ? "•••• " + cardNumber.substring(cardNumber.length() - 4) : cardNumber;
                } else {
                    maskedCardNumber = "";
                }
                cardIcon.setImageResource(R.drawable.mastercard);
                textCardNumber.setText(maskedCardNumber);
            } else if ("Cash".equals(paymentMethod)) {
                cardIcon.setImageResource(R.drawable.cash);
                textCardNumber.setText("Cash");
            } else if ("Apple Pay".equals(paymentMethod)) {
                cardIcon.setImageResource(R.drawable.apple_pay);
                textCardNumber.setText("Apple Pay");
            } else if ("Paypal".equals(paymentMethod)) {
                cardIcon.setImageResource(R.drawable.paypal);
                textCardNumber.setText("PayPal");
            }
        }
    }
}