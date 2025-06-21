package com.example.pp0101markov.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pp0101markov.R;
import com.example.pp0101markov.models.Service;

import java.util.List;

public class ServiceRecyclerAdapter extends RecyclerView.Adapter<ServiceRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Service> serviceList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Service service, int position);
    }

    public ServiceRecyclerAdapter(Context context, List<Service> serviceList, OnItemClickListener listener) {
        this.context = context;
        this.serviceList = serviceList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ServiceRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_main_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceRecyclerAdapter.ViewHolder holder, int position) {
        Service service = serviceList.get(position);

        Glide.with(context)
                .load(service.getAvatar_url())
                .placeholder(R.drawable.basic_pedicure)
                .into(holder.imageService);

        holder.textTitle.setText(service.getName());
        holder.textPrice.setText("$" + String.format("%.2f", service.getPrice()));

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(service, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageService;
        TextView textTitle;
        TextView textPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageService = itemView.findViewById(R.id.imageService);
            textTitle = itemView.findViewById(R.id.textTitle);
            textPrice = itemView.findViewById(R.id.textPrice);
        }
    }
}