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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pp0101markov.adapters.DayAdapter;
import com.example.pp0101markov.models.DayItem;

import java.util.ArrayList;
import java.util.List;

public class MasterBookingActivity extends AppCompatActivity {

    private ListView listDays;
    private DayAdapter dayAdapter;
    private List<DayItem> dayItems;

    private GridLayout gridAvailability;
    private Button btnBook;
    ImageView PrevBtn;

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
        PrevBtn=findViewById(R.id.previousBtn);
        dayItems = new ArrayList<>();
        dayItems.add(new DayItem(1, "Sun"));
        dayItems.add(new DayItem(2, "Mon"));
        dayItems.add(new DayItem(3, "Tue"));
        dayItems.add(new DayItem(4, "Wed"));
        dayItems.add(new DayItem(5, "Thu"));
        dayItems.add(new DayItem(6, "Fri"));
        dayItems.add(new DayItem(7, "Sat"));
        dayItems.add(new DayItem(8, "Sun"));
        dayItems.add(new DayItem(9, "Mon"));
        dayItems.add(new DayItem(10, "Tue"));
        dayItems.add(new DayItem(11, "Wed"));
        dayItems.add(new DayItem(12, "Thu"));
        dayItems.add(new DayItem(13, "Fri"));
        dayItems.add(new DayItem(14, "Sat"));
        dayItems.add(new DayItem(15, "Sun"));
        dayItems.add(new DayItem(16, "Mon"));
        dayItems.add(new DayItem(17, "Tue"));
        dayItems.add(new DayItem(18, "Wed"));
        dayItems.add(new DayItem(19, "Thu"));
        dayItems.add(new DayItem(20, "Fri"));
        dayItems.add(new DayItem(21, "Sun"));
        dayItems.add(new DayItem(22, "Mon"));
        dayItems.add(new DayItem(23, "Tue"));
        dayItems.add(new DayItem(24, "Wed"));
        dayItems.add(new DayItem(25, "Thu"));
        dayItems.add(new DayItem(26, "Fri"));
        dayItems.add(new DayItem(27, "Sat"));
        dayItems.add(new DayItem(28, "Sat"));
        dayItems.add(new DayItem(29, "Sun"));
        dayItems.add(new DayItem(30, "Mon"));
        dayItems.add(new DayItem(31, "Tue"));
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
        PrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MasterBookingActivity.this, Board4Activity.class);
                startActivity(intent);
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
