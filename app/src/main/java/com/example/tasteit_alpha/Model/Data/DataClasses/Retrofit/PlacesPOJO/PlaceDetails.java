package com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceDetails implements Parcelable {


    @SerializedName("business_status")
    @Expose
    private String businessStatus;
    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;
    @SerializedName("formatted_phone_number")
    @Expose
    private String formattedPhoneNumber;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("international_phone_number")
    @Expose
    private String internationalPhoneNumber;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos = null;
    @SerializedName("reviews")
    @Expose
    private final List<Review> reviews = null;
    @SerializedName("types")
    @Expose
    private List<String> types = null;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("price_level")
    @Expose
    private Integer priceLevel;
    @SerializedName("vicinity")
    @Expose
    private String vicinity;
    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("place_id")
    @Expose
    private String placeId;


    protected PlaceDetails(Parcel in) {
        businessStatus = in.readString();
        formattedAddress = in.readString();
        formattedPhoneNumber = in.readString();
        icon = in.readString();
        internationalPhoneNumber = in.readString();
        name = in.readString();
        photos = in.createTypedArrayList(Photo.CREATOR);
        types = in.createStringArrayList();
        url = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readDouble();
        }
        if (in.readByte() == 0) {
            priceLevel = null;
        } else {
            priceLevel = in.readInt();
        }
        vicinity = in.readString();
        website = in.readString();
        placeId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(businessStatus);
        dest.writeString(formattedAddress);
        dest.writeString(formattedPhoneNumber);
        dest.writeString(icon);
        dest.writeString(internationalPhoneNumber);
        dest.writeString(name);
        dest.writeTypedList(photos);
        dest.writeStringList(types);
        dest.writeString(url);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(rating);
        }
        if (priceLevel == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(priceLevel);
        }
        dest.writeString(vicinity);
        dest.writeString(website);
        dest.writeString(placeId);
    }

    public PlaceDetails() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlaceDetails> CREATOR = new Creator<PlaceDetails>() {
        @Override
        public PlaceDetails createFromParcel(Parcel in) {
            return new PlaceDetails(in);
        }

        @Override
        public PlaceDetails[] newArray(int size) {
            return new PlaceDetails[size];
        }
    };

    public String getBusinessStatus() {
        return businessStatus;
    }


    public String getFormattedAddress() {
        return formattedAddress;
    }


    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public String getPlaceId() {
        return placeId;
    }


    public String getIcon() {
        return icon;
    }


    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }



    public String getName() {
        return name;
    }


    public OpeningHours getOpeningHours() {
        return openingHours;
    }


    public List<Photo> getPhotos() {
        return photos;
    }



    public List<Review> getReviews() {
        return reviews;
    }


    public List<String> getTypes() {
        return types;
    }


    public String getUrl() {
        return url;
    }


    public Double getRating() {
        return rating;
    }



    public Integer getPriceLevel() {
        return priceLevel;
    }


    public String getVicinity() {
        return vicinity;
    }



    public String getWebsite() {
        return website;
    }




    //to String for debug
    @NotNull
    @Override
    public String toString() {
        return "PlaceDetails{" +
                "businessStatus='" + businessStatus + '\'' +
                ", formattedAddress='" + formattedAddress + '\'' +
                ", formattedPhoneNumber='" + formattedPhoneNumber + '\'' +
                ", icon='" + icon + '\'' +
                ", internationalPhoneNumber='" + internationalPhoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", openingHours=" + openingHours +
                ", photos=" + photos +
                ", reviews=" + reviews +
                ", types=" + types +
                ", url='" + url + '\'' +
                ", rating=" + rating +
                ", priceLevel=" + priceLevel +
                ", vicinity='" + vicinity + '\'' +
                ", website='" + website + '\'' +
                ", placeId='" + placeId + '\'' +
                '}';
    }
}
