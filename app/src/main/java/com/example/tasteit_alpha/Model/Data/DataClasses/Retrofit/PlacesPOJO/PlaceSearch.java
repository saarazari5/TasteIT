package com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceSearch {


    @SerializedName("place_id")
    @Expose
    private String placeId;


    public String getPlaceId() {
        return placeId;
    }


}

