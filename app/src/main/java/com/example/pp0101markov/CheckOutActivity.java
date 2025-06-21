package com.example.pp0101markov;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pp0101markov.models.Transaction;

public class CheckOutActivity extends AppCompatActivity {
    private static final int REQUEST_PAYMENT_METHOD = 101;
    private static final int REQUEST_ADD_CARD = 102;

    private TextView tvDate, tvTime, tvService, tvLocation, tvPayment, tvTotal;
    private ImageView ivPaymentIcon;
    private Button btnBook;
    private View paymentRow;

    private String paymentMethod = "";
    private String paymentDisplay = "";
    private int paymentIconRes = 0;
    private double totalAmount = 0;
    private int orderId;
    private String profileId;

    // Для карты
    private String cardNumberMasked = "";
    private String cardholderName = "";
    private String expDate = "";
    private String cvv = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        tvDate = findViewById(R.id.textDate);
        tvTime = findViewById(R.id.textTime);
        tvService = findViewById(R.id.textService);
        tvLocation = findViewById(R.id.textLocation);
        tvPayment = findViewById(R.id.textPayment);
        tvTotal = findViewById(R.id.textTotal);
        ivPaymentIcon = findViewById(R.id.paymentIcon);
        btnBook = findViewById(R.id.buttonBook);
        paymentRow = findViewById(R.id.paymentRow);
        Intent intent = getIntent();
        orderId = (int)intent.getLongExtra("order_id", 0);
        profileId = intent.getStringExtra("profile_id");
        String date = intent.getStringExtra("day");
        String time = intent.getStringExtra("time");
        String service = intent.getStringExtra("service");
        String location = intent.getStringExtra("address");
        totalAmount = intent.getDoubleExtra("price", 0);
        tvDate.setText(date);
        tvTime.setText(time);
        tvService.setText(service);
        tvLocation.setText(location);
        tvTotal.setText("$" + String.valueOf(totalAmount));

        tvLocation.setMovementMethod(LinkMovementMethod.getInstance());
        tvLocation.setOnClickListener(v -> {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("geo:0,0?q=" + location));
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) startActivity(mapIntent);
        });

        // Start with no payment method selected
        updatePaymentUI();

        paymentRow.setOnClickListener(v -> {
            Intent pmIntent = new Intent(CheckOutActivity.this, PaymentMethodActivity.class);
            pmIntent.putExtra("card_number_masked", cardNumberMasked);
            pmIntent.putExtra("cardholder_name", cardholderName);
            pmIntent.putExtra("exp_date", expDate);
            pmIntent.putExtra("cvv", cvv);
            startActivityForResult(pmIntent, REQUEST_PAYMENT_METHOD);
        });
        btnBook.setOnClickListener(v -> {
            if (paymentMethod.isEmpty()) {
                Toast.makeText(this, R.string.choose_a_payment_method, Toast.LENGTH_SHORT).show();
                return;
            }
            // 1. Создать транзакцию
            createTransaction(orderId,profileId, paymentMethod, totalAmount);
        });
    }

    private void updatePaymentUI() {
        switch (paymentMethod) {
            case "Card":
                paymentDisplay = cardNumberMasked.isEmpty() ? getString(R.string.add_a_card) : cardNumberMasked;
                paymentIconRes = R.drawable.mastercard;
                break;
            case "Cash":
                paymentDisplay = "Cash";
                paymentIconRes = R.drawable.cash;
                break;
            case "Apple Pay":
                paymentDisplay = "Apple Pay";
                paymentIconRes = R.drawable.apple_pay;
                break;
            case "Paypal":
                paymentDisplay = "PayPal";
                paymentIconRes = R.drawable.paypal;
                break;
            default:
                paymentDisplay = "";
                paymentIconRes = 0;
                break;
        }
        tvPayment.setText(paymentDisplay);
        if (paymentIconRes != 0) {
            ivPaymentIcon.setImageResource(paymentIconRes);
            ivPaymentIcon.setVisibility(View.VISIBLE);
        } else {
            ivPaymentIcon.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PAYMENT_METHOD && resultCode == Activity.RESULT_OK && data != null) {
            paymentMethod = data.getStringExtra("payment_method");
            if ("Card".equals(paymentMethod)) {
                cardNumberMasked = data.getStringExtra("card_number_masked");
                cardholderName = data.getStringExtra("cardholder_name");
                expDate = data.getStringExtra("exp_date");
                cvv = data.getStringExtra("cvv");
                // Если карта не введена — запускаем AddCardActivity
                if (cardNumberMasked == null || cardNumberMasked.isEmpty()) {
                    Intent addCardIntent = new Intent(this, AddCardActivity.class);
                    startActivityForResult(addCardIntent, REQUEST_ADD_CARD);
                    return;
                }
            }
            updatePaymentUI();
        }
        if (requestCode == REQUEST_ADD_CARD && resultCode == Activity.RESULT_OK && data != null) {
            cardNumberMasked = data.getStringExtra("card_number_masked");
            cardholderName = data.getStringExtra("cardholder_name");
            expDate = data.getStringExtra("exp_date");
            cvv = data.getStringExtra("cvv");
            paymentMethod = "Card";
            updatePaymentUI();
        }
    }



    public void createTransaction(int orderId, String profileId, String paymentMethod, double amount) {
        SupabaseClient supabaseClient=new SupabaseClient();
        supabaseClient.setContext(this);
        supabaseClient.createTransaction(orderId, profileId, paymentMethod, amount, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
            }

            @Override
            public void onResponse(String responseBody) {
                updateIsPaid(orderId, true);
            }
        });
    }
    public void updateIsPaid(int orderId, boolean isPaid) {
        SupabaseClient supabaseClient=new SupabaseClient();
        supabaseClient.setContext(this);
        supabaseClient.setOrderPaid(orderId, isPaid, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() ->  Toast.makeText(CheckOutActivity.this, R.string.the_payment_was_successful, Toast.LENGTH_SHORT).show());
                startActivity(new Intent(CheckOutActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}