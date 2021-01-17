package com.example.tasteit_alpha.ui.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_alpha.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<PhotosRvAdapter.ViewHolder> {
    List<String>mockData;
    public InfoAdapter(List<String>mockData) {
        this.mockData=mockData;
    }

    @NonNull
    @Override
    public PhotosRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return new PhotosRvAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_photo_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosRvAdapter.ViewHolder holder, int position) {
        Picasso.get().load(mockData.get(position)).fit().placeholder(R.mipmap.ic_launcher_foreground).into(holder.detailsPhoto);
    }

    @Override
    public int getItemCount() {
        return mockData.size();
    }
}
