package com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceDetailsContainer {
    @SerializedName("result")
    @Expose
    private PlaceDetails result;
    @SerializedName("status")
    @Expose
    private String status;

    public PlaceDetails getResult() {
        return result;
    }

    public String getStatus() {
        return status;
    }

}
