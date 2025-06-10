package com.example.pp0101markov;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pp0101markov.models.DayItem;

import java.util.ArrayList;
import java.util.List;

public class MasterBookingActivity extends AppCompatActivity {

    private ListView listDays;
    private DayAdapter dayAdapter;
    private List<DayItem> dayItems;

    private GridLayout gridAvailability;
    private Button btnBook;

    private String selectedTime = null;
    private int selectedDayPosition = -1;

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

        listDays = findViewById(R.id.listDays);
        gridAvailability = findViewById(R.id.gridAvailability);
        btnBook = findViewById(R.id.btnBook);

        dayItems = new ArrayList<>();
        dayItems.add(new DayItem(17, "Sun"));
        dayItems.add(new DayItem(18, "Mon"));
        dayItems.add(new DayItem(19, "Tue"));
        dayItems.add(new DayItem(20, "Wed"));
        dayItems.add(new DayItem(21, "Thu"));
        dayItems.add(new DayItem(22, "Fri"));
        dayItems.add(new DayItem(23, "Sat"));

        dayAdapter = new DayAdapter(this, dayItems);
        listDays.setAdapter(dayAdapter);

        listDays.setOnItemClickListener((parent, view, position, id) -> {
            selectedDayPosition = position;
            dayAdapter.setSelectedPosition(position);
            updateBookButtonState();
        });
        createTimeButtons();

        btnBook.setOnClickListener(v -> {
            if (selectedDayPosition >= 0 && selectedTime != null) {
                DayItem selectedDay = dayItems.get(selectedDayPosition);
                String message = "Booked on " + selectedDay.dayName + ", " + selectedDay.dayNumber + " at " + selectedTime;
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });
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
            View child = gridAvailability.getChildAt(i);
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
