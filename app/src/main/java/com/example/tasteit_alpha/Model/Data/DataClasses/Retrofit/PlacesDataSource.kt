package com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit

import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.PlaceDetailsContainer
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.PlacesResponse
import com.example.tasteit_alpha.Utils.AppUtils.API_KEY
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object  PlacesDataSource {
  private const val baseUrl="https://maps.googleapis.com/maps/api/"
    private val placesApiClient:PlacesApiClient= Retrofit.Builder()
        .baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build().create(PlacesApiClient::class.java)

    suspend fun getPlaceSearch(address:String):Response<PlacesResponse>{
        return placesApiClient.getPlaceSearch(API_KEY,address,"textquery")
    }

    suspend fun getPlaceDetails(placeID:String):Response<PlaceDetailsContainer>{
        return placesApiClient.getPlaceDetails(API_KEY,placeID)
    }

    suspend fun getPlacesByText(pageToken:String, query: String):Response<PlacesResponse>{
        return placesApiClient.getPlacesByText(pageToken, API_KEY , query)
    }
}