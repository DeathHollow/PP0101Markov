package com.example.pp0101markov.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pp0101markov.R;
import com.example.pp0101markov.models.DayItem;

import java.util.List;

public class DayRecyclerAdapter extends RecyclerView.Adapter<DayRecyclerAdapter.DayViewHolder> {

    private final List<DayItem> days;
    private int selectedPosition = -1;
    private final OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(int position);
    }

    public DayRecyclerAdapter(List<DayItem> days, OnDayClickListener listener) {
        this.days = days;
        this.listener = listener;
    }

    public void setSelectedPosition(int pos) {
        int old = selectedPosition;
        selectedPosition = pos;
        notifyItemChanged(old);
        notifyItemChanged(pos);
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        DayItem day = days.get(position);
        holder.tvDayNumber.setText(String.valueOf(day.dayNumber));
        holder.tvDayName.setText(day.dayName);

        if (position == selectedPosition) {
            holder.container.setBackgroundResource(R.drawable.day_item_bg_selected);
            holder.tvDayNumber.setTextColor(Color.parseColor("#D94F4F"));
            holder.tvDayName.setTextColor(Color.parseColor("#D94F4F"));
        } else {
            holder.container.setBackgroundResource(R.drawable.day_item_bg_default);
            holder.tvDayNumber.setTextColor(Color.parseColor("#333333"));
            holder.tvDayName.setTextColor(Color.parseColor("#888888"));
        }
        holder.container.setOnClickListener(v -> {
            if (listener != null) listener.onDayClick(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayNumber, tvDayName;
        LinearLayout container;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            container = (LinearLayout) itemView;
        }
    }
}