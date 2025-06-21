package com.example.pp0101markov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pp0101markov.adapters.ServiceRecyclerAdapter;
import com.example.pp0101markov.models.Booking;
import com.example.pp0101markov.models.Service;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView userNameTextView, upcomingTitleText, upcomingDateDay, upcomingDateMonth, upcomingDateTime;
    private TabLayout tabLayout;
    private RecyclerView servicesRecyclerView;
    private ServiceRecyclerAdapter servicesAdapter;
    private List<Service> allServicesList;
    private List<Service> serviceList;
    private CardView upcomingCard;
    private Button editUpcomingButton;
    private Booking upcomingBooking;
    private ImageButton menuButton;
    private SupabaseClient supabaseClient = new SupabaseClient();
    private static final int REQUEST_CODE_PROFILE = 101;
    private SearchView searchView;

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
        searchView = findViewById(R.id.searchView);

        upcomingCard = findViewById(R.id.upcomingCard);
        upcomingTitleText = findViewById(R.id.upcomingTitleText);
        upcomingDateDay = findViewById(R.id.upcomingDateDay);
        upcomingDateMonth = findViewById(R.id.upcomingDateMonth);
        upcomingDateTime = findViewById(R.id.upcomingDateTime);
        editUpcomingButton = findViewById(R.id.editUpcomingButton);

        userNameTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PROFILE);
        });
        menuButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SalonDetailActivity.class)));
        allServicesList = new ArrayList<>();
        serviceList = new ArrayList<>();
        servicesAdapter = new ServiceRecyclerAdapter(this, serviceList, (service, position) -> {
            Double servicePrice = service.getPrice();
            String serviceId = service.getId();
            String serviceName = service.getName();
            Intent intent = new Intent(MainActivity.this, Board4Activity.class);
            intent.putExtra("service_id", serviceId);
            intent.putExtra("category_id", Integer.toString(service.getCategory_id()));
            intent.putExtra("service_name", serviceName);
            intent.putExtra("service_price", servicePrice);
            startActivity(intent);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        servicesRecyclerView.setLayoutManager(layoutManager);
        servicesRecyclerView.setAdapter(servicesAdapter);

        setupTabs();
        setupSearchView();
        loadUserNameFromProfile();
        loadServices();
        loadUpcomingBooking();
    }

    private void setupTabs() {
        String[] categories = {"Nails", "Hair", "Massage", "Eyebrows"};
        for (String cat : categories) {
            tabLayout.addTab(tabLayout.newTab().setText(cat));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                String selected = tab.getText().toString();
                filterServicesByQueryAndCategory(searchView.getQuery().toString(), selected);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterServicesByQueryAndCategory(query, getSelectedCategory());
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterServicesByQueryAndCategory(newText, getSelectedCategory());
                return true;
            }
        });
    }

    private void loadServices() {
        supabaseClient.getServices(new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, R.string.error_loading_services, Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    try {
                        JSONArray array = new JSONArray(responseBody);
                        allServicesList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject s = array.getJSONObject(i);
                            String id = s.optString("id");
                            String imgUrl = s.optString("avatar_url", "");
                            String name = s.optString("name", "Service");
                            double price = s.optDouble("price", 0);
                            int category = s.optInt("category_id", 1);
                            allServicesList.add(new Service(id, imgUrl, name, price, category));
                        }
                        filterServicesByQueryAndCategory("", "Nails");
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, R.string.error_parsing_services, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void filterServicesByQueryAndCategory(String query, String category) {
        serviceList.clear();
        int catId = getCategoryIdByName(category);
        for (Service s : allServicesList) {
            boolean matchesCategory = s.getCategory_id() == catId;
            boolean matchesQuery = s.getName().toLowerCase().contains(query.toLowerCase());
            if (matchesCategory && matchesQuery) {
                serviceList.add(s);
            }
        }
        servicesAdapter.notifyDataSetChanged();
    }

    private int getCategoryIdByName(String category) {
        switch (category) {
            case "Nails": return 1;
            case "Hair": return 2;
            case "Massage": return 3;
            case "Eyebrows": return 4;
            default: return 1;
        }
    }

    private String getSelectedCategory() {
        int position = tabLayout.getSelectedTabPosition();
        if (position >= 0) {
            TabLayout.Tab tab = tabLayout.getTabAt(position);
            if (tab != null && tab.getText() != null)
                return tab.getText().toString();
        }
        return "Nails";
    }

    private void loadUserNameFromProfile() {
        String userId = DataBinding.getUuidUser();
        if (userId == null || userId.isEmpty()) {
            userNameTextView.setText("User");
            return;
        }
        supabaseClient.getProfile(userId, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> userNameTextView.setText("User"));
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    try {
                        JSONArray array = new JSONArray(responseBody);
                        if (array.length() > 0) {
                            JSONObject profile = array.getJSONObject(0);
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

    private void showCancelDialog(Booking booking) {
        DialogCancelBooking.show(this, () -> cancelBooking(booking));
    }

    private void cancelBooking(Booking booking) {
        supabaseClient.cancelBooking(String.valueOf(booking.getId()), new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, R.string.couldn_t_cancel, Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, R.string.booking_canceled, Toast.LENGTH_SHORT).show();
                    loadUpcomingBooking();
                });
            }
        });
    }

    private void loadUpcomingBooking() {
        String userId = DataBinding.getUuidUser();
        if (userId == null || userId.isEmpty()) {
            upcomingCard.setVisibility(View.GONE);
            return;
        }
        supabaseClient.getBookings(userId, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
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
                        List<JSONObject> upcomingList = new ArrayList<>();
                        Date now = new Date();
                        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String dateStr = obj.optString("date");
                            Date bookingDate = null;
                            try { bookingDate = isoFormat.parse(dateStr); } catch (ParseException ignored) {}
                            if (bookingDate != null && !bookingDate.before(now)) {
                                upcomingList.add(obj);
                            }
                        }
                        if (upcomingList.isEmpty()) {
                            upcomingCard.setVisibility(View.GONE);
                            return;
                        }
                        Collections.sort(upcomingList, (a, b) -> {
                            try {
                                String da = a.getString("date");
                                String db = b.getString("date");
                                return da.compareTo(db);
                            } catch (Exception e) { return 0; }
                        });

                        JSONObject nextBookingObj = upcomingList.get(0);
                        Booking nextBooking = new Gson().fromJson(nextBookingObj.toString(), Booking.class);
                        upcomingBooking = nextBooking;

                        String name = nextBooking.getName();
                        String dateTimeStr = nextBooking.getDate();
                        Date d = isoFormat.parse(dateTimeStr);

                        String day = (d != null) ? new SimpleDateFormat("dd", Locale.getDefault()).format(d) : "";
                        String month = (d != null) ? new SimpleDateFormat("MMM", Locale.ENGLISH).format(d) : "";
                        String weekday = (d != null) ? new SimpleDateFormat("EEEE", Locale.ENGLISH).format(d) : "";
                        String time = (d != null) ? new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(d) : "";

                        upcomingCard.setVisibility(View.VISIBLE);
                        upcomingTitleText.setText(name);
                        upcomingDateDay.setText(day);
                        upcomingDateMonth.setText(month);
                        String timeString = weekday + (time.isEmpty() ? "" : ", " + time);
                        upcomingDateTime.setText(timeString);

                        editUpcomingButton.setOnClickListener(v -> {
                            if (upcomingBooking != null) {
                                showCancelDialog(upcomingBooking);
                            }
                        });
                    } catch (Exception e) {
                        upcomingCard.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PROFILE && resultCode == RESULT_OK && data != null) {
            boolean bookingChanged = data.getBooleanExtra("booking_changed", false);
            if (bookingChanged) {
                loadUpcomingBooking();
            }
        }
    }
}