package com.example.pp0101markov;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pp0101markov.adapters.DayRecyclerAdapter;
import com.example.pp0101markov.models.DayItem;

import java.util.ArrayList;
import java.util.List;

public class MasterBookingActivity extends AppCompatActivity {

    private List<DayItem> dayItems;

    private GridLayout gridAvailability;
    private Button btnBook;
    ImageView PrevBtn;
    private TextView tvName, tvProfession, tvRating;

    private String selectedTime = null;
    private int selectedDayPosition = -1;

    // Данные для бронирования
    private String userId;
    private String masterId;
    private String serviceId; // если нужно
    private String Avatar;
    private ImageView imgProfile;
    private RecyclerView recyclerDays;
    private DayRecyclerAdapter dayAdapter;

    private final String[] times = {
            "10:00 am", "11:00 am",
            "01:30 pm", "03:00 pm",
            "07:00 pm", "05:00 pm"
    };

    private final boolean[] enabledTimes = {
            false, true,
            false, true,
            true, false
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_booking);
        String masterName = getIntent().getStringExtra("master_name");
        String masterCategory = getIntent().getStringExtra("master_category");
        double masterReviews = getIntent().getDoubleExtra("master_reviews", 0.0);
        imgProfile=findViewById(R.id.imgProfile);
        tvName=findViewById(R.id.tvName);
        tvRating=findViewById(R.id.tvRating);
        TextView tvProfession = findViewById(R.id.tvProfession);
        String professionText;
        switch ((masterCategory != null ? masterCategory : "").toLowerCase()) {
            case "1":
                professionText = "Nail Designer";
                break;
            case "2":
                professionText = "Hair Stylist";
                break;
            case "3":
                professionText = "Masseur";
                break;
            case "4":
                professionText = "Brow Master";
                break;
            default:
                professionText = "Specialist";
                break;
        }
        tvProfession.setText(professionText);
        tvName.setText(masterName);
        tvRating.setText(String.valueOf(masterReviews));
        Avatar=getIntent().getStringExtra("avatar_url");
        if (Avatar != null && !Avatar.isEmpty()) {
            Glide.with(this)
                    .load(Avatar)
                    .placeholder(R.drawable.master_mcmiller)
                    .error(R.drawable.master_mcmiller)
                    .circleCrop()
                    .into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.master_mcmiller);
        }
        gridAvailability = findViewById(R.id.gridAvailability);
        btnBook = findViewById(R.id.btnBook);
        PrevBtn = findViewById(R.id.previousBtn);

        // Получение данных для бронирования из Intent/глобальных данных
        userId = DataBinding.getUuidUser();
        masterId = getIntent().getStringExtra("master_id");
        serviceId = getIntent().getStringExtra("service_id");

        recyclerDays = findViewById(R.id.listDays);
        recyclerDays.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        dayItems = new ArrayList<>();
        String[] weekDays = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        int dayNumber = 1;
        for (int i = 0; i < 31; i++) {
            dayItems.add(new DayItem(dayNumber, weekDays[(i%7)]));
            dayNumber++;
        }

        dayAdapter = new DayRecyclerAdapter(dayItems, pos -> {
            selectedDayPosition = pos;
            dayAdapter.setSelectedPosition(pos);
            updateBookButtonState();
        });
        recyclerDays.setAdapter(dayAdapter);
        createTimeButtons();

        btnBook.setOnClickListener(v -> {
            if (selectedDayPosition >= 0 && selectedTime != null) {
                DayItem selectedDay = dayItems.get(selectedDayPosition);
                String dayString = String.format("%02d %s", selectedDay.dayNumber, selectedDay.dayName);

                SupabaseClient supabaseClient = new SupabaseClient();
                btnBook.setEnabled(false);
                supabaseClient.setContext(this);
                supabaseClient.createBooking(
                        userId,
                        dayString,
                        selectedTime,
                        masterId,
                        new SupabaseClient.SBC_Callback() {
                            @Override
                            public void onFailure(java.io.IOException e) {
                                runOnUiThread(() -> {
                                    btnBook.setEnabled(true);
                                    Toast.makeText(MasterBookingActivity.this, "Ошибка бронирования: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                            }

                            @Override
                            public void onResponse(String responseBody) {
                                runOnUiThread(() -> {
                                    Toast.makeText(MasterBookingActivity.this, "Бронирование успешно!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MasterBookingActivity.this, ConfirmBookingActivity.class);
                                    intent.putExtra("day", dayString);
                                    intent.putExtra("time", selectedTime);
                                    intent.putExtra("address", "8502 Preston Rd. Inglewood");
                                    startActivity(intent);
                                    finish();
                                });
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Выберите день и время", Toast.LENGTH_SHORT).show();
            }
        });

        PrevBtn.setOnClickListener(v -> finish());

        updateBookButtonState();
    }

    private void createTimeButtons() {
        gridAvailability.removeAllViews();

        for (int i = 0; i < times.length; i++) {
            final int index = i;
            Button btnTime = new Button(this);
            btnTime.setText(times[i]);
            btnTime.setTextSize(14f);
            btnTime.setAllCaps(false);
            btnTime.setPadding(0,0,0,0);
            btnTime.setMinHeight(0);
            btnTime.setMinWidth(0);
            btnTime.setBackgroundResource(R.drawable.btn_time_bg);
            btnTime.setEnabled(enabledTimes[i]);
            btnTime.setTextColor(enabledTimes[i] ? Color.parseColor("#333333") : Color.parseColor("#CCCCCC"));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(i % 3, 1f);
            params.rowSpec = GridLayout.spec(i / 3);
            params.setMargins(8, 8, 8, 8);
            btnTime.setLayoutParams(params);

            btnTime.setOnClickListener(v -> {
                if (!btnTime.isEnabled()) return;
                selectedTime = times[index];
                updateTimeButtonsSelection(btnTime);
                updateBookButtonState();
            });

            gridAvailability.addView(btnTime);
        }
    }

    private void updateTimeButtonsSelection(Button selectedButton) {
        int count = gridAvailability.getChildCount();
        for (int i = 0; i < count; i++) {
            android.view.View child = gridAvailability.getChildAt(i);
            if (child instanceof Button) {
                Button btn = (Button) child;
                if (btn == selectedButton) {
                    btn.setBackgroundResource(R.drawable.btn_time_selected_bg);
                    btn.setTextColor(Color.parseColor("#D94F4F"));
                } else {
                    btn.setBackgroundResource(R.drawable.btn_time_bg);
                    btn.setTextColor(btn.isEnabled() ? Color.parseColor("#333333") : Color.parseColor("#CCCCCC"));
                }
            }
        }
    }

    private void updateBookButtonState() {
        btnBook.setEnabled(selectedDayPosition >= 0 && selectedTime != null);
        btnBook.setBackgroundTintList(ColorStateList.valueOf(
                btnBook.isEnabled() ? Color.parseColor("#D94F4F") : Color.parseColor("#FFC0C0")
        ));
    }
}
