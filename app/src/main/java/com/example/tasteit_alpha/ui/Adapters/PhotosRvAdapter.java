package com.example.tasteit_alpha.ui.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.Photo;
import com.example.tasteit_alpha.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotosRvAdapter extends  RecyclerView.Adapter<PhotosRvAdapter.ViewHolder> {


    private final List<Photo> photoList;
    private final MutableLiveData<Photo> mSelectedPhoto;
    private final Context context;
    private final boolean isForSearch;
    public PhotosRvAdapter(List<Photo> photoList, MutableLiveData<Photo>mSelectedPhoto , Context context , boolean isForShowingInGrid) {
        this.photoList = photoList;
        this.mSelectedPhoto = mSelectedPhoto;
        this.context=context;
        this.isForSearch = isForShowingInGrid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(isForSearch) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_photo_search, parent, false));
        }else {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_photo_details, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        if(isForSearch) {
            deScaleImage(photo.getFullReference(), holder.detailsPhoto);
        }else {
            Picasso.get().load(photo.getFullReference()).fit().into(holder.detailsPhoto);
        }
        //handle click event
        holder.detailsPhoto.setOnClickListener(view ->{
                if(mSelectedPhoto==null)return;
                mSelectedPhoto.postValue(photo);
        });

    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    static class ViewHolder extends  RecyclerView.ViewHolder{
        ImageView detailsPhoto;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            detailsPhoto =itemView.findViewById(R.id.details_photo);
        }
    }

    private void deScaleImage(String url, ImageView imageView) {
        Glide.with(context)
                .asBitmap().load(url).apply(new RequestOptions().placeholder(R.mipmap.ic_launcher))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                float width = resource.getWidth();
                float height = resource.getHeight();
                float aspectRatio =  (width / height);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(resource ,480 , Math.round(480/aspectRatio), false);
                imageView.setImageBitmap(scaledBitmap);
            }
            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {}
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(isForSearch) {
            return position;
        }else{
            return 0;
        }
    }
}
