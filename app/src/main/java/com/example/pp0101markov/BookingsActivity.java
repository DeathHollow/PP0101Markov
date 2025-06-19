package com.example.pp0101markov;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pp0101markov.models.Booking;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class BookingsActivity extends AppCompatActivity {

    private ListView listViewBookings;
    private Button buttonPast, buttonUpcoming;
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

        listViewBookings.setOnItemClickListener((parent, view, position, id) -> {
            if (isUpcomingView) {
                showCancelDialog(upcomingBookings.get(position));
            }
        });

        fetchBookings();
    }

    private void fetchBookings() {
        // Call your SupabaseClient to get bookings
        SupabaseClient supabaseClient = new SupabaseClient();
        String userId = DataBinding.getUuidUser();
        supabaseClient.getBookings(userId, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Toast.makeText(BookingsActivity.this, "Error fetching bookings", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    BookingResponse bookingResponse = new Gson().fromJson(responseBody, BookingResponse.class);
                    pastBookings = bookingResponse.getPastBookings();
                    upcomingBookings = bookingResponse.getUpcomingBookings();
                    showUpcomingBookings(); // Default view
                });
            }
        });
    }

    private void showPastBookings() {
        isUpcomingView = false;
        ArrayAdapter<Booking> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pastBookings);
        listViewBookings.setAdapter(adapter);
    }

    private void showUpcomingBookings() {
        isUpcomingView = true;
        ArrayAdapter<Booking> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, upcomingBookings);
        listViewBookings.setAdapter(adapter);
    }

    private void showCancelDialog(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Appointment")
                .setMessage("Are you sure you want to cancel this appointment?")
                .setPositiveButton("Cancel", (dialog, which) -> cancelBooking(booking.getId()))
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelBooking(String bookingId) {
        SupabaseClient supabaseClient = new SupabaseClient();
        supabaseClient.cancelBooking(bookingId, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Toast.makeText(BookingsActivity.this, "Error canceling booking", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Toast.makeText(BookingsActivity.this, "Booking canceled", Toast.LENGTH_SHORT).show();
                    fetchBookings(); // Refresh bookings
                });
            }
        });
    }
}

