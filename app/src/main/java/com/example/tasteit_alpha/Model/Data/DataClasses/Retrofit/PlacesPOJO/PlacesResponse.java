package com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
 public class PlacesResponse {
    @SerializedName(value = "candidates",alternate = {"results"})
    @Expose
    private List<PlaceSearch> placeSearches = null;

    @SerializedName("next_page_token")
    @Expose
    private String nextPageToken="";

    @SerializedName("status")
    @Expose
    private String status="";

     public String getStatus() {
         return status;
     }


     public String getNextPageToken() {
         return nextPageToken;
     }

     public List<PlaceSearch> getPlaceSearches() {
        return placeSearches;
    }



}
