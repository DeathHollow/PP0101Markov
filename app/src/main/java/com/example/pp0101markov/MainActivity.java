package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pp0101markov.adapters.ServicesAdapter;
import com.example.pp0101markov.models.Service;
import com.example.pp0101markov.models.Service_main;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView userNameTextView, upcomingTitleText, upcomingDateDay, upcomingDateMonth, upcomingDateTime;
    private TabLayout tabLayout;
    private RecyclerView servicesRecyclerView;
    private ServicesAdapter servicesAdapter;
    private List<Service_main> serviceList;
    private CardView upcomingCard;
    private Button editUpcomingButton;
    private ImageButton menuButton;
    private SupabaseClient supabaseClient = new SupabaseClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supabaseClient.setContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        userNameTextView = findViewById(R.id.userNameTextView);
        tabLayout = findViewById(R.id.tabLayout);
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
        menuButton = findViewById(R.id.menuButton);

        upcomingCard = findViewById(R.id.upcomingCard);
        upcomingTitleText = findViewById(R.id.upcomingTitleText);
        upcomingDateDay = findViewById(R.id.upcomingDateDay);
        upcomingDateMonth = findViewById(R.id.upcomingDateMonth);
        upcomingDateTime = findViewById(R.id.upcomingDateTime);
        editUpcomingButton = findViewById(R.id.editUpcomingButton);

        // Навигация
        userNameTextView.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        menuButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SalonDetailActivity.class)));

        setupTabs();
        setupServicesRecyclerView();
        loadUserNameFromProfile();
        loadUpcomingBooking();
    }

    private void loadUserNameFromProfile() {
        String userId = DataBinding.getUuidUser();
        if (userId == null || userId.isEmpty()) {
            userNameTextView.setText("User");
            return;
        }
        supabaseClient.getProfile(userId, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
                runOnUiThread(() -> userNameTextView.setText("User"));
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    try {
                        org.json.JSONArray array = new org.json.JSONArray(responseBody);
                        if (array.length() > 0) {
                            org.json.JSONObject profile = array.getJSONObject(0);
                            String fullName = profile.optString("full_name", "User");
                            userNameTextView.setText(fullName);
                        } else {
                            userNameTextView.setText("User");
                        }
                    } catch (Exception e) {
                        userNameTextView.setText("User");
                    }
                });
            }
        });
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Recommended"));
        tabLayout.addTab(tabLayout.newTab().setText("Packages"));
        tabLayout.addTab(tabLayout.newTab().setText("Professionals"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(MainActivity.this, "Selected: " + tab.getText(), Toast.LENGTH_SHORT).show();
                // По желанию — фильтрация сервисов по категории
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupServicesRecyclerView() {
        serviceList = new ArrayList<>();
        servicesAdapter = new ServicesAdapter(this, serviceList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        servicesRecyclerView.setLayoutManager(layoutManager);
        servicesRecyclerView.setAdapter(servicesAdapter);

        // Подгружаем сервисы из Supabase
        supabaseClient.getServices(new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error loading services", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(String responseBody) {
                // Преобразуем ответ в список Service_main
                runOnUiThread(() -> {
                    try {
                        JSONArray array = new JSONArray(responseBody);
                        serviceList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject s = array.getJSONObject(i);
                            String name = s.optString("name", "Service");
                            double price = s.optDouble("price", 0);
                            String duration = s.has("duration") ? s.optString("duration") : "60 mins"; // если duration есть в бд
                            String id = s.optString("id");
                            String imgUrl = s.has("avatar_url") ? s.optString("avatar_url") : "";
                            // Приведи imgUrl к нужному виду, если нужно (например, через getAvatar_url())
                            serviceList.add(new Service_main(name, duration, "$" + (int)price, R.drawable.basic_pedicure));
                        }
                        servicesAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error parsing services", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadUpcomingBooking() {
        // Получаем все бронирования пользователя и находим ближайшее (по дате)
        String userId = DataBinding.getUuidUser();
        if (userId == null || userId.isEmpty()) {
            upcomingCard.setVisibility(View.GONE);
            return;
        }
        supabaseClient.getBookings(userId, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(java.io.IOException e) {
                runOnUiThread(() -> upcomingCard.setVisibility(View.GONE));
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    try {
                        JSONArray array = new JSONArray(responseBody);
                        if (array.length() == 0) {
                            upcomingCard.setVisibility(View.GONE);
                            return;
                        }
                        // Найти ближайшее бронирование по дате
                        JSONObject nextBooking = null;
                        Date now = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        List<JSONObject> upcomingList = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String dateStr = obj.optString("date");
                            Date bookingDate = null;
                            try { bookingDate = sdf.parse(dateStr); } catch (ParseException ignored) {}
                            if (bookingDate != null && !bookingDate.before(now)) {
                                upcomingList.add(obj);
                            }
                        }
                        if (upcomingList.isEmpty()) {
                            upcomingCard.setVisibility(View.GONE);
                            return;
                        }
                        // Сортируем по дате (возрастание)
                        Collections.sort(upcomingList, new Comparator<JSONObject>() {
                            public int compare(JSONObject a, JSONObject b) {
                                try {
                                    String da = a.getString("date");
                                    String db = b.getString("date");
                                    return da.compareTo(db);
                                } catch (Exception e) { return 0; }
                            }
                        });
                        nextBooking = upcomingList.get(0);
                        String name = nextBooking.optString("name", "Service");
                        String date = nextBooking.optString("date");
                        String time = ""; // Если есть поле time, подставь сюда
                        if (nextBooking.has("time")) time = nextBooking.optString("time");
                        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
                        Date d = sdf.parse(date);
                        String day = (d != null) ? new SimpleDateFormat("dd", Locale.getDefault()).format(d) : "";
                        String month = (d != null) ? monthFormat.format(d) : "";
                        String weekday = (d != null) ? new SimpleDateFormat("EEEE", Locale.ENGLISH).format(d) : "";

                        upcomingCard.setVisibility(View.VISIBLE);
                        upcomingTitleText.setText(name);
                        upcomingDateDay.setText(day);
                        upcomingDateMonth.setText(month);
                        String timeString = weekday + (time.isEmpty() ? "" : ", " + time);
                        upcomingDateTime.setText(timeString);

                        editUpcomingButton.setOnClickListener(v -> {
                            // Открыть экран редактирования бронирования (реализуй при необходимости)
                            Toast.makeText(MainActivity.this, "Edit booking (not implemented)", Toast.LENGTH_SHORT).show();
                        });
                    } catch (Exception e) {
                        upcomingCard.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}