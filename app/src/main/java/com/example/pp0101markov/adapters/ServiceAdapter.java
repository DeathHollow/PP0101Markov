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
import com.example.pp0101markov.models.Service;

import java.util.List;

public class ServiceAdapter extends BaseAdapter {
    private Context context;
    private List<Service> serviceList;
    private LayoutInflater inflater;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return serviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return serviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView imageService;
        TextView textTitle;
        TextView textPrice;
        ImageView imageArrow;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_service, parent, false);
            holder = new ViewHolder();
            holder.imageService = convertView.findViewById(R.id.imageService);
            holder.textTitle = convertView.findViewById(R.id.textTitle);
            holder.textPrice = convertView.findViewById(R.id.textPrice);
            holder.imageArrow = convertView.findViewById(R.id.imageArrow);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Service service = serviceList.get(position);

        Glide.with(context)
                .load(service.getAvatar_url())
                .placeholder(R.drawable.basic_pedicure)
                .into(holder.imageService);

        holder.textTitle.setText(service.getName());
        holder.textPrice.setText("$" + String.format("%.2f", service.getPrice()));
        holder.imageArrow.setImageResource(R.drawable.arrow_right);

        return convertView;
    }
}