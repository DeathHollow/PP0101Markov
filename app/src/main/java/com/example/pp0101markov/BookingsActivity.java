package com.example.pp0101markov;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pp0101markov.models.Booking;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
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
    }

    private void fetchBookings() {
        SupabaseClient supabaseClient = new SupabaseClient();
        String profileId = DataBinding.getUuidUser();
        supabaseClient.getBookings(profileId, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Toast.makeText(BookingsActivity.this, "Error fetching bookings", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Type listType = new TypeToken<List<Booking>>(){}.getType();
                    allBookings = new Gson().fromJson(responseBody, listType);
                    splitBookingsByDate();
                    showUpcomingBookings(); // default
                });
            }
        });
    }

    private void splitBookingsByDate() {
        pastBookings.clear();
        upcomingBookings.clear();
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (Booking booking : allBookings) {
            try {
                Date bookingDate = sdf.parse(booking.getDate());
                if (bookingDate.before(now)) {
                    pastBookings.add(booking);
                } else {
                    upcomingBookings.add(booking);
                }
            } catch (Exception ignored) {
                // Если ошибка парсинга — не добавляем
            }
        }
    }

    private void showPastBookings() {
        isUpcomingView = false;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, toDisplayList(pastBookings));
        listViewBookings.setAdapter(adapter);
    }

    private void showUpcomingBookings() {
        isUpcomingView = true;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, toDisplayList(upcomingBookings));
        listViewBookings.setAdapter(adapter);
    }

    private List<String> toDisplayList(List<Booking> bookings) {
        List<String> list = new ArrayList<>();
        for (Booking b : bookings) {
            list.add("Order #" + b.getId() +
                    "\nName: " + b.getName() +
                    "\nDate: " + b.getDate() +
                    "\nPrice: $" + b.getPrice());
        }
        return list;
    }
}