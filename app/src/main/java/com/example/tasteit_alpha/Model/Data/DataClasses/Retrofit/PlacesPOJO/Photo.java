package com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.example.tasteit_alpha.Model.Data.DataClasses.PresentAble;
import com.example.tasteit_alpha.Utils.AppUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class Photo implements PresentAble , Parcelable {

    @SerializedName("height")
    @Expose
    private Integer height;
    @SerializedName("html_attributions")
    @Expose
    private List<String> htmlAttributions = null;
    @SerializedName("photo_reference")
    @Expose
    private final String photoReference;
    @SerializedName("width")
    @Expose
    private Integer width;


    private PlaceDetails relatedPlaceDetails;
    private String fullReference;

    public PlaceDetails getRelatedPlaceDetails() {
        return relatedPlaceDetails;
    }

    public void setRelatedPlaceDetails(PlaceDetails relatedPlaceDetails) {
        this.relatedPlaceDetails = relatedPlaceDetails;
    }


    public String getFullReference() {
        return  AppUtils.BASE_PHOTO_URL+ photoReference + "&key=" + AppUtils.API_KEY;
    }

    protected Photo(Parcel in) {
        if (in.readByte() == 0) {
            height = null;
        } else {
            height = in.readInt();
        }
        htmlAttributions = in.createStringArrayList();
        photoReference = in.readString();
        if (in.readByte() == 0) {
            width = null;
        } else {
            width = in.readInt();
        }
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (height == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(height);
        }
        parcel.writeStringList(htmlAttributions);
        parcel.writeString(photoReference);
        if (width == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(width);
        }
    }
}
