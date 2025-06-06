package com.example.pp0101markov;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MasterAdapter extends BaseAdapter {

    private Context context;
    private List<Master> masterList;
    private LayoutInflater inflater;

    public MasterAdapter(Context context, List<Master> masterList) {
        this.context = context;
        this.masterList = masterList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return masterList.size();
    }

    @Override
    public Object getItem(int position) {
        return masterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView imageMaster;
        TextView textName;
        TextView textSpecialization;
        TextView textRating;
        ImageView imageArrow;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_master, parent, false);
            holder = new ViewHolder();
            holder.imageMaster = convertView.findViewById(R.id.imageMaster);
            holder.textName = convertView.findViewById(R.id.textName);
            holder.textSpecialization = convertView.findViewById(R.id.textSpecialization);
            holder.textRating = convertView.findViewById(R.id.textRating);
            holder.imageArrow = convertView.findViewById(R.id.imageArrow);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Master master = masterList.get(position);
        holder.imageMaster.setImageResource(master.getImageResId());
        holder.textName.setText(master.getName());
        holder.textSpecialization.setText(master.getSpecialization());
        holder.textRating.setText(String.format("★ %.1f", master.getRating()));

        return convertView;
    }
}