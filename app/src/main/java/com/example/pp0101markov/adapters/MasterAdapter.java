package com.example.pp0101markov.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pp0101markov.R;
import com.example.pp0101markov.models.Master;

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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Master master = masterList.get(position);

        Glide.with(context)
                .load(master.getAvatar_url())
                .placeholder(R.drawable.master_mcmiller)
                .into(holder.imageMaster);

        holder.textName.setText(master.getName());
        holder.textRating.setText(String.format("★ %.1f", master.getReviews()));
        switch (master.getCategory_id()) {
            case "1":
                holder.textSpecialization.setText(R.string.nail_designer);
                break;
            case "2":
                holder.textSpecialization.setText(R.string.hair_stylist);
                break;
            case "3":
                holder.textSpecialization.setText(R.string.masseur);
                break;
            case "4":
                holder.textSpecialization.setText(R.string.brow_master);
                break;
            default:
                holder.textSpecialization.setText(R.string.specialist);
                break;
        }

        return convertView;
    }
}