package com.example.pp0101markov;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ConfirmBookingActivity extends AppCompatActivity {

    private TextView tvDayTime, tvAddress, tvMainPage;
    private Button btnKeepBooking;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_booking_activity);

        tvDayTime = findViewById(R.id.tvDayTime);
        tvAddress = findViewById(R.id.tvAddress);
        btnKeepBooking = findViewById(R.id.btnKeepBooking);
        tvMainPage = findViewById(R.id.tvMainPage);

        // Получаем данные из Intent
        Intent intent = getIntent();
        String day = intent.getStringExtra("day");
        String time = intent.getStringExtra("time");
        String address = intent.getStringExtra("address");

        // Устанавливаем текст
        tvDayTime.setText(day + "  " + time);
        tvAddress.setText(address);
        tvAddress.setMovementMethod(LinkMovementMethod.getInstance());

        // Клик по адресу — открываем карту (Google Maps)
        tvAddress.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        });

        // Кнопка "Keep booking" — возвращаемся назад (например, к экрану выбора)
        btnKeepBooking.setOnClickListener(v -> finish());

        // Текст "Main page" — идём на главный экран приложения
        tvMainPage.setOnClickListener(v -> {
            Intent mainIntent = new Intent(ConfirmBookingActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            finish();
        });
    }
}
