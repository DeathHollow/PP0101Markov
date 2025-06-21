package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pp0101markov.DataBinding;
import com.example.pp0101markov.DialogCancelBooking;
import com.example.pp0101markov.SupabaseClient;
import com.example.pp0101markov.adapters.BookingListAdapter;
import com.example.pp0101markov.models.Booking;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingsActivity extends AppCompatActivity {

    private ListView listViewBookings;
    private Button buttonPast, buttonUpcoming;
    private List<Booking> allBookings = new ArrayList<>();
    private List<Booking> pastBookings = new ArrayList<>();
    private List<Booking> upcomingBookings = new ArrayList<>();
    private boolean isUpcomingView = true;
    private BookingListAdapter adapter;
    private boolean bookingChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        listViewBookings = findViewById(R.id.listView_bookings);
        buttonPast = findViewById(R.id.button_past);
        buttonUpcoming = findViewById(R.id.button_upcoming);

        buttonPast.setOnClickListener(view -> showPastBookings());
        buttonUpcoming.setOnClickListener(view -> showUpcomingBookings());

        fetchBookings();
        ImageView prevBtn = findViewById(R.id.previousBtn);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("booking_changed", bookingChanged);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void fetchBookings() {
        SupabaseClient supabaseClient = new SupabaseClient();
        supabaseClient.setContext(this);
        String profileId = DataBinding.getUuidUser();
        supabaseClient.getBookings(profileId, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Toast.makeText(BookingsActivity.this, R.string.error_fetching_bookings, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    try {
                        Type listType = new TypeToken<List<Booking>>(){}.getType();
                        allBookings = new Gson().fromJson(responseBody, listType);
                        splitBookingsByDate();
                        if (isUpcomingView) {
                            showUpcomingBookings();
                        } else {
                            showPastBookings();
                        }
                    } catch (Exception e) {
                        Toast.makeText(BookingsActivity.this, R.string.error_parsing_bookings, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Поддержка даты "yyyy-MM-dd" и "yyyy-MM-dd'T'HH:mm:ss"
    private void splitBookingsByDate() {
        pastBookings.clear();
        upcomingBookings.clear();
        Date now = new Date();
        SimpleDateFormat[] dateFormats = new SimpleDateFormat[] {
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        };
        for (Booking booking : allBookings) {
            Date bookingDate = null;
            for (SimpleDateFormat sdf : dateFormats) {
                try {
                    bookingDate = sdf.parse(booking.getDate());
                    if (bookingDate != null) break;
                } catch (ParseException ignored) {}
            }
            if (bookingDate == null) continue;
            if (bookingDate.before(now)) {
                pastBookings.add(booking);
            } else {
                upcomingBookings.add(booking);
            }
        }
    }

    private void showPastBookings() {
        isUpcomingView = false;
        adapter = new BookingListAdapter(this, pastBookings, false, null);
        listViewBookings.setAdapter(adapter);
        buttonPast.setEnabled(false);
        buttonUpcoming.setEnabled(true);
    }

    private void showUpcomingBookings() {
        isUpcomingView = true;
        adapter = new BookingListAdapter(this, upcomingBookings, true, booking -> {
            DialogCancelBooking.show(this, () -> cancelBooking(booking));
        });
        listViewBookings.setAdapter(adapter);
        buttonUpcoming.setEnabled(false);
        buttonPast.setEnabled(true);
    }

    private void cancelBooking(Booking booking) {
        SupabaseClient supabaseClient = new SupabaseClient();
        supabaseClient.setContext(this);
        supabaseClient.cancelBooking(String.valueOf(booking.getId()), new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Toast.makeText(BookingsActivity.this, R.string.cancel_failed, Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Toast.makeText(BookingsActivity.this, R.string.booking_cancelled, Toast.LENGTH_SHORT).show();
                    bookingChanged = true;
                    fetchBookings();
                });
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("booking_changed", bookingChanged);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}