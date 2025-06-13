package com.example.pp0101markov;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pp0101markov.models.DayItem;

import java.util.List;

public class DayAdapter extends BaseAdapter {

    private final Context context;
    private final List<DayItem> days;
    private int selectedPosition = -1;

    public DayAdapter(Context context, List<DayItem> days) {
        this.context = context;
        this.days = days;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public int getCount() {
        return days.size();
    }

    @Override
    public Object getItem(int position) {
        return days.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView tvDayNumber, tvDayName;
        LinearLayout container;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_day, parent, false);
            holder = new ViewHolder();
            holder.tvDayNumber = convertView.findViewById(R.id.tvDayNumber);
            holder.tvDayName = convertView.findViewById(R.id.tvDayName);
            holder.container = (LinearLayout) convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

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

        return convertView;
    }
}
