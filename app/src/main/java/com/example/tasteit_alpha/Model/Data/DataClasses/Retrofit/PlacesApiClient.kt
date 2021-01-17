package com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit

import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.PlaceDetailsContainer
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.PlacesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
//base url: https://maps.googleapis.com/maps/api/place/
interface PlacesApiClient {
@GET("place/findplacefromtext/json?fields=place_id")
  suspend fun getPlaceSearch(@Query("key",encoded = true)apiKey:String, @Query("input" ,encoded = true)input:String
                             , @Query("inputtype",encoded = true )inputType:String):Response<PlacesResponse>

  @GET("place/details/json?")
    suspend fun getPlaceDetails(@Query("key",encoded = true)apiKey:String,@Query("place_id",encoded = true)place_id:String):Response<PlaceDetailsContainer>


    @GET("https://maps.googleapis.com/maps/api/place/textsearch/json?type=restaurant")
    suspend fun getPlacesByText(@Query("pagetoken",encoded = true)pageToken:String,@Query("key",encoded = true)apiKey:String,
    @Query("query")query: String):Response<PlacesResponse>

}
