package com.example.pp0101markov.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.pp0101markov.R;
import com.example.pp0101markov.models.Booking;

import java.util.List;

public class BookingListAdapter extends BaseAdapter {
    private final Context context;
    private final List<Booking> bookings;
    private final boolean isUpcoming;
    private final OnCancelClickListener cancelClickListener;

    public interface OnCancelClickListener {
        void onCancelClick(Booking booking);
    }

    public BookingListAdapter(Context context, List<Booking> bookings, boolean isUpcoming, OnCancelClickListener cancelClickListener) {
        this.context = context;
        this.bookings = bookings;
        this.isUpcoming = isUpcoming;
        this.cancelClickListener = cancelClickListener;
    }

    @Override
    public int getCount() { return bookings.size(); }
    @Override
    public Object getItem(int position) { return bookings.get(position); }
    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Booking booking = bookings.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_booking_card, parent, false);
        }

        TextView tvSalon = convertView.findViewById(R.id.tvSalon);
        TextView tvDesc = convertView.findViewById(R.id.tvDescription);
        TextView tvDate = convertView.findViewById(R.id.tvDate);
        TextView tvPrice = convertView.findViewById(R.id.tvPrice);
        Button btnCancel = convertView.findViewById(R.id.btnCancel);

        String salon = "The Gallery Salon";
        String desc = booking.getName();
        String date = booking.getDate();
        String price = "$" + booking.getPrice();

        tvSalon.setText(salon);
        tvDesc.setText(desc);
        tvDate.setText(date);
        tvPrice.setText(price);

        btnCancel.setVisibility(isUpcoming ? View.VISIBLE : View.GONE);
        btnCancel.setOnClickListener(v -> {
            if (cancelClickListener != null) cancelClickListener.onCancelClick(booking);
        });

        return convertView;
    }
}