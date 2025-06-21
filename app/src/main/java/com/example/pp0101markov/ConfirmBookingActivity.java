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
    private Button btnCheckOut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_booking_activity);

        tvDayTime = findViewById(R.id.tvDayTime);
        tvAddress = findViewById(R.id.tvAddress);
        btnCheckOut = findViewById(R.id.btnKeepBooking);
        tvMainPage = findViewById(R.id.tvMainPage);
        Intent intent = getIntent();
        long orderId = intent.getLongExtra("order_id", 0);
        String userId = intent.getStringExtra("profile_id");
        String day = intent.getStringExtra("day");
        String time = intent.getStringExtra("time");
        String address = intent.getStringExtra("address");
        String serviceId = intent.getStringExtra("service");
        double price = intent.getDoubleExtra("price", 0);
        tvDayTime.setText(day + "  " + time);
        tvAddress.setText(address);
        tvAddress.setMovementMethod(LinkMovementMethod.getInstance());
        tvAddress.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        });
        btnCheckOut.setOnClickListener(v -> {
            Intent checkoutIntent = new Intent(ConfirmBookingActivity.this, CheckOutActivity.class);
            checkoutIntent.putExtra("order_id", orderId);
            checkoutIntent.putExtra("profile_id", userId);
            checkoutIntent.putExtra("day", day);
            checkoutIntent.putExtra("time", time);
            checkoutIntent.putExtra("address", address);
            checkoutIntent.putExtra("service", serviceId);
            checkoutIntent.putExtra("price", price);
            checkoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(checkoutIntent);
            finish();
        });
        tvMainPage.setOnClickListener(v -> {
            Intent mainIntent = new Intent(ConfirmBookingActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            finish();
        });
    }
}
