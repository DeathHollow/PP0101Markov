package com.example.pp0101markov.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pp0101markov.R;
import com.example.pp0101markov.models.Service_main;

import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder> {

    private List<Service_main> serviceList;
    private Context context;

    public ServicesAdapter(Context context, List<Service_main> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_item, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service_main service = serviceList.get(position);
        holder.serviceNameTextView.setText(service.getName());
        holder.serviceDurationTextView.setText(service.getDuration());
        holder.servicePriceTextView.setText(service.getPrice());
        holder.serviceImageView.setImageResource(service.getImageResId());
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {

        ImageView serviceImageView;
        TextView serviceNameTextView, serviceDurationTextView, servicePriceTextView;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImageView = itemView.findViewById(R.id.serviceImageView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            serviceDurationTextView = itemView.findViewById(R.id.serviceDurationTextView);
            servicePriceTextView = itemView.findViewById(R.id.servicePriceTextView);
        }
    }
}
