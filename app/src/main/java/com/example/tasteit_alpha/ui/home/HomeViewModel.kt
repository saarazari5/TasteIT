package com.example.tasteit_alpha.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tasteit_alpha.Model.Data.Callbacks.ImagesCallback
import com.example.tasteit_alpha.Model.DataRepository
import com.example.tasteit_alpha.Model.Data.DataClasses.ImageDatum
import com.example.tasteit_alpha.Model.Data.DataClasses.PresentAble
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.Photo
import com.example.tasteit_alpha.ui.home.HomeFragment.*
import java.util.*

class HomeViewModel : ViewModel() ,ImagesCallback {
    val selectedImage: MutableLiveData<ImageDatum> = MutableLiveData()
    val mImages: MutableLiveData<List<PresentAble>?> = MutableLiveData()
    val selectedPhoto : MutableLiveData<Photo> = MutableLiveData()

    override fun onResponse(imagesData: List<PresentAble>?, exc: Exception?) {
        if(exc!=null)return
            mImages.postValue(imagesData)
            return
    }

    fun fetchDataByDistance(distance : Int,currentUserLong: Double, currentUserLat: Double) {
        DataRepository.fetchDataByGeoQuery(distance , currentUserLat, currentUserLong, this)
    }

    fun fetchDataByLocality(locality: String?) {
        if(locality.isNullOrEmpty())return
        DataRepository.fetchDataByLocality(locality, this@HomeViewModel)
    }

}












