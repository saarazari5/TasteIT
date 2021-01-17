package com.example.tasteit_alpha.ui.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.tasteit_alpha.Model.Data.DataClasses.ImageDatum;
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.PlaceDetails;
import com.example.tasteit_alpha.Model.DataRepository;
import com.example.tasteit_alpha.R;
import com.example.tasteit_alpha.Utils.AppUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.example.tasteit_alpha.Utils.AppUtils.DEFAULT_LANG_LAT;
import static com.example.tasteit_alpha.Utils.AppUtils.getDistanceBetweenTwoPoints;

public class ImageDataAdapter extends RecyclerView.Adapter<ImageDataAdapter.ViewHolder> {
    private final List<ImageDatum> data;
    private final double userLong;
    private final double userLat;
    private final Context context;
    MutableLiveData<ImageDatum> mImgSelected;

    public ImageDataAdapter(Collection<ImageDatum> data, double userLong, double userLat, Context context, MutableLiveData<ImageDatum> mImgSelected) {
        this.mImgSelected = mImgSelected;
        this.data = new ArrayList<>(data);
        this.userLong = userLong;
        this.userLat = userLat;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_image, parent, false));

    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageDatum img = data.get(position);
        fetchImage(img,holder.imageView);
        sendImgToDetailsIfClicked(holder.imageView , img);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.distanceTooltip.setOnClickListener(view -> Toast.makeText(context,"press long click to initiate distance check" , Toast.LENGTH_SHORT).show());
            fetchDistanceText(img , holder.distanceTooltip);
        }else{
            holder.distanceTooltip.setVisibility(View.INVISIBLE);
        }

        fetchLikes(holder,img);
    }

    private void fetchLikes(ViewHolder holder, ImageDatum img) {
        String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DataRepository.INSTANCE.isUserLiked(currentUserID, img.getFilePath(), isUserLike -> holder.likeBtn.setLiked(isUserLike));
        setLikeEvent(holder.likeBtn, img , currentUserID);
    }

    private void sendImgToDetailsIfClicked(ImageView imageView, ImageDatum img) {
        imageView.setOnClickListener(view -> mImgSelected.postValue(img));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchDistanceText(ImageDatum img, ImageView distanceTooltip) {
        PlaceDetails placeDetails = img.getRelatedPlaceDetails();
        if (userLat != DEFAULT_LANG_LAT && userLong != DEFAULT_LANG_LAT) {
           distanceTooltip.setTooltipText(distanceCheck(img.getImageLat(),img.getImageLong()));
        }else{
            if(placeDetails!=null) {
                distanceTooltip.setTooltipText(placeDetails.getName());
            }
        }
    }


    private void fetchImage(ImageDatum img, ImageView imageView) {
        if (!img.getUriString().equals("")) {
            deScaleImage(Uri.parse(img.getUriString()), imageView, img);
        } else {
            DataRepository.INSTANCE.insertImageFromStorage(img.getFilePath(), uri -> {
                if (imageView == null) return;
                deScaleImage(uri, imageView, img);
                if (uri != null) {
                    img.setUriString(uri.toString());
                    DataRepository.INSTANCE.updateImageUri(img.getFilePath(), img.getUriString());
                }
            });
        }
    }

    private void setLikeEvent(LikeButton likeBtn, ImageDatum img, String currentUserID) {
       likeBtn.setOnLikeListener(new OnLikeListener() {
           @Override
           public void liked(LikeButton likeButton) {
               DataRepository.INSTANCE.addLike(currentUserID, img , context);
               img.setLikes(img.getLikes() + 1);
           }

           @Override
           public void unLiked(LikeButton likeButton) {
               DataRepository.INSTANCE.removeLike(currentUserID, img , context);
               img.setLikes(img.getLikes() - 1);
           }
       });
    }

    private void insertImageWithoutScaling(Uri uri, int imageWidth, int imageHeight, ImageView imageView) {
        Glide.with(context).load(uri)
                .apply(new RequestOptions().placeholder(R.mipmap.ic_launcher).override(imageWidth, imageHeight).centerCrop())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(imageView);
    }


    @SuppressLint("DefaultLocale")
    private String distanceCheck(double imgLat , double imgLong) {
        float distance = getDistanceBetweenTwoPoints(imgLat, imgLong, userLat, userLong);
        if (distance < 100) {
            return "this place is:" + " "+ String.format("%.1f", distance) + "m"+ " "+ "from you";
        } else {
            return "this place is:"+ " "+ String.format("%.1f", distance / 1000)  + "km" + " "+"from you";
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }




    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        LikeButton likeBtn;
        ImageView moreInfo;
        ImageView distanceTooltip;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.mainImg);
            likeBtn = itemView.findViewById(R.id.btn_like);
            moreInfo = itemView.findViewById(R.id.btn_info);
            distanceTooltip = itemView.findViewById(R.id.img_distance_check);

        }
    }


    private void deScaleImage(Uri uri, ImageView imageView, ImageDatum img) {
        if (img.getImageWidth() != AppUtils.DEF_IMAGE_HW) {
            insertImageWithoutScaling(uri, img.getImageWidth(), img.getImageHeight(), imageView);
        } else {
            Glide.with(context)
                    .asBitmap().load(uri).apply(new RequestOptions().placeholder(R.mipmap.ic_launcher))
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    float width = resource.getWidth();
                    float height = resource.getHeight();
                    float aspectRatio = (width / height);
                    img.setImageWidth(getRandomNumber(300, 760));
                    img.setImageHeight(Math.round(img.getImageWidth() / aspectRatio));
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(resource, img.getImageWidth(), img.getImageHeight(), false);
                    imageView.setImageBitmap(scaledBitmap);
                    DataRepository.INSTANCE.updateImageScale(img.getFilePath(), img.getImageWidth(), img.getImageHeight());
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                }
            });
        }
    }


    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

}



