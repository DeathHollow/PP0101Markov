package com.example.pp0101markov;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SalonDetailActivity extends AppCompatActivity {

    private static final String SALON_PHONE_NUMBER = "+79681020329";
    private static final String SALON_SMS_NUMBER = "+79681020329";
    private static final double SALON_LATITUDE = 54.978855;
    ImageView prevBtn;
    private static final double SALON_LONGITUDE = 73.377346;
    private static final String SALON_SHARE_TEXT = "Check out The Gallery Salon! Located at 8502 Preston Rd, Inglewood.";

    private LinearLayout callButton, messageButton, directionsButton, shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_profile);

        callButton = findViewById(R.id.callButton);
        messageButton = findViewById(R.id.messageButton);
        directionsButton = findViewById(R.id.directionsButton);
        shareButton = findViewById(R.id.shareButton);
        prevBtn = findViewById(R.id.previousBtn);

        callButton.setOnClickListener(v -> makePhoneCall());
        messageButton.setOnClickListener(v -> sendSms());
        directionsButton.setOnClickListener(v -> openMap());
        shareButton.setOnClickListener(v -> shareSalon());

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void makePhoneCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + SALON_PHONE_NUMBER));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_app_found_to_make_calls, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSms() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("sms:" + SALON_SMS_NUMBER));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_app_found_to_send_sms, Toast.LENGTH_SHORT).show();
        }
    }

    private void openMap() {
        String geoUri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(The Gallery Salon)",
                SALON_LATITUDE, SALON_LONGITUDE, SALON_LATITUDE, SALON_LONGITUDE);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            String mapsUrl = String.format(Locale.ENGLISH,
                    "https://www.google.com/maps/search/?api=1&query=%f,%f", SALON_LATITUDE, SALON_LONGITUDE);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
            startActivity(browserIntent);
        }
    }

    private void shareSalon() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Salon Share Text", SALON_SHARE_TEXT);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, R.string.salon_info_copied_to_clipboard, Toast.LENGTH_SHORT).show();
    }
}
