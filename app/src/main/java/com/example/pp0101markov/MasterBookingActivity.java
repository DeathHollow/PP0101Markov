package com.example.pp0101markov;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MasterBookingActivity extends AppCompatActivity {

    private List<DayItem> dayItems;

    private GridLayout gridAvailability;
    private Button btnBook;
    ImageView PrevBtn;
    private TextView tvName, tvProfession, tvRating;

    private String selectedTime = null;
    private int selectedDayPosition = -1;

    private String userId;
    private String masterId;
    private String serviceId;
    private String Avatar;
    private ImageView imgProfile,prevBtn;
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
        prevBtn=findViewById(R.id.previousBtn);
        String masterCategory = getIntent().getStringExtra("master_category");
        double masterReviews = getIntent().getDoubleExtra("master_reviews", 0.0);
        double price = getIntent().getDoubleExtra("service_price", 0);
        serviceId=getIntent().getStringExtra("service_id");
        masterId=getIntent().getStringExtra("master_id");
        String serviceName = getIntent().getStringExtra("service_name");
        imgProfile=findViewById(R.id.imgProfile);
        tvName=findViewById(R.id.tvName);
        tvRating=findViewById(R.id.tvRating);
        TextView tvProfession = findViewById(R.id.tvProfession);
        String professionText;
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MasterBookingActivity.this, Board4Activity.class);
                startActivity(intent);
            }
        });
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
        userId = DataBinding.getUuidUser();
        masterId = getIntent().getStringExtra("master_id");
        serviceId = getIntent().getStringExtra("service_id");

        recyclerDays = findViewById(R.id.listDays);
        recyclerDays.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        dayItems = new ArrayList<>();
        String[] weekDays = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 14; i++) {
            int dayNumber = calendar.get(Calendar.DAY_OF_MONTH);
            String weekDay = weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1];
            dayItems.add(new DayItem(dayNumber, weekDay));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
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
                Calendar bookingDay = Calendar.getInstance();
                bookingDay.add(Calendar.DAY_OF_MONTH, selectedDayPosition);
                int year = bookingDay.get(Calendar.YEAR);
                int month = bookingDay.get(Calendar.MONTH) + 1;
                int day = bookingDay.get(Calendar.DAY_OF_MONTH);
                String dayString = String.format("%04d-%02d-%02d", year, month, day);
                DateFormat inputFormat = new SimpleDateFormat("hh:mm a", Locale.US);
                DateFormat outputFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
                Date date = null;
                try {
                    date = inputFormat.parse(selectedTime);
                } catch (ParseException e) {
                    Toast.makeText(this, R.string.time_error, Toast.LENGTH_SHORT).show();
                    btnBook.setEnabled(true);
                    return;
                }
                String time24 = outputFormat.format(date);

                String dateTime = dayString + "T" + time24;

                SupabaseClient supabaseClient = new SupabaseClient();
                supabaseClient.setContext(this);
                btnBook.setEnabled(false);
                supabaseClient.createBooking(
                        "The Gallery Salon",
                        dateTime,
                        masterCategory,
                        userId,
                        masterId,
                        serviceId,
                        price,
                        new SupabaseClient.SBC_Callback() {
                            @Override
                            public void onFailure(java.io.IOException e) {
                                runOnUiThread(() -> {
                                    btnBook.setEnabled(true);
                                    Toast.makeText(MasterBookingActivity.this, getString(R.string.booking_error) + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                            }
                            @Override
                            public void onResponse(String responseBody) {
                                runOnUiThread(() -> {
                                    try {
                                        com.google.gson.JsonArray arr = new com.google.gson.JsonParser().parse(responseBody).getAsJsonArray();
                                        com.google.gson.JsonObject obj = arr.size() > 0 ? arr.get(0).getAsJsonObject() : null;
                                        long orderId = obj != null && obj.has("id") ? obj.get("id").getAsLong() : 0;
                                        Toast.makeText(MasterBookingActivity.this, R.string.the_booking_is_successful, Toast.LENGTH_SHORT).show();
                                        Intent confirmBooking = new Intent(MasterBookingActivity.this, ConfirmBookingActivity.class);
                                        confirmBooking.putExtra("order_id", orderId);
                                        confirmBooking.putExtra("profile_id", userId);
                                        confirmBooking.putExtra("day", dayString);
                                        confirmBooking.putExtra("time", selectedTime);
                                        confirmBooking.putExtra("address", "8502 Preston Rd. Inglewood");
                                        confirmBooking.putExtra("service", serviceName + " with " + masterName);
                                        confirmBooking.putExtra("price", price);
                                        startActivity(confirmBooking);
                                        finish();
                                    } catch (Exception e) {
                                        Toast.makeText(MasterBookingActivity.this, getString(R.string.error_parsing_the_response) + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                );
            } else {
                Toast.makeText(this, R.string.choose_the_day_and_time, Toast.LENGTH_SHORT).show();
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
