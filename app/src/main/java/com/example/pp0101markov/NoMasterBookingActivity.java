package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

// Пример активности для бронирования без выбора мастера
public class NoMasterBookingActivity extends AppCompatActivity {

    private SupabaseClient supabaseClient;
    private Button btnBook;

    // Пример выбранных значений — в вашем приложении замените на реальные данные из UI
    private String selectedDay = "2024-07-01"; // формат YYYY-MM-DD
    private Slot selectedSlot = new Slot("10:00", "MasterName");

    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nomaster_booking); // ваш layout

        btnBook = findViewById(R.id.btnBook);

        supabaseClient = new SupabaseClient();
        supabaseClient.setContext(this);

        userId = DataBinding.getUuidUser(); // Получаем userId из вашего хранилища

        btnBook.setOnClickListener(view -> {
            if (selectedDay != null && selectedSlot != null) {
                btnBook.setEnabled(false); // блокируем кнопку, чтобы избежать повторных нажатий
                supabaseClient.createBooking(userId, selectedDay, selectedSlot.time, selectedSlot.withPerson, new SupabaseClient.SBC_Callback() {
                    @Override
                    public void onFailure(IOException e) {
                        runOnUiThread(() -> {
                            btnBook.setEnabled(true);
                            Toast.makeText(NoMasterBookingActivity.this, "Booking failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }

                    @Override
                    public void onResponse(String responseBody) {
                        runOnUiThread(() -> Toast.makeText(NoMasterBookingActivity.this, "Booking successful!", Toast.LENGTH_SHORT).show());
                        Intent intent = new Intent(NoMasterBookingActivity.this, ConfirmBookingActivity.class);
                        intent.putExtra("day",selectedDay);
                        intent.putExtra("time", selectedSlot);
                        intent.putExtra("address", "8502 Preston Rd. Inglewood");
                        startActivity(intent);

                        // После успешного создания бронирования получаем список бронирований пользователя
                        supabaseClient.getBookings(userId, new SupabaseClient.SBC_Callback() {
                            @Override
                            public void onFailure(IOException e) {
                                runOnUiThread(() -> Toast.makeText(NoMasterBookingActivity.this, "Failed to load bookings: " + e.getMessage(), Toast.LENGTH_LONG).show());
                            }

                            @Override
                            public void onResponse(String responseBody) {
                                runOnUiThread(() -> {
                                    Toast.makeText(NoMasterBookingActivity.this, "Loaded bookings: " + responseBody, Toast.LENGTH_LONG).show();
                                    // TODO: здесь можно распарсить JSON и обновить UI
                                });
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(NoMasterBookingActivity.this, "Please select day and time slot", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class Slot {
        String time;
        String withPerson;

        Slot(String time, String withPerson) {
            this.time = time;
            this.withPerson = withPerson;
        }
    }
}
