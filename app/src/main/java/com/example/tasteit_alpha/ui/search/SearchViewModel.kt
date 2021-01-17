package com.example.tasteit_alpha.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasteit_alpha.Model.Data.DataClasses.ImageDatum
import com.example.tasteit_alpha.Model.Data.DataClasses.PresentAble
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.Photo
import com.example.tasteit_alpha.Model.DataRepository
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    val selectedImage: MutableLiveData<ImageDatum> = MutableLiveData()
    val mImages: MutableLiveData<List<PresentAble>?> = MutableLiveData()
    val selectedPhoto : MutableLiveData<Photo> = MutableLiveData()

    fun fetchDataQuery(searchQuery: String) {
       viewModelScope.launch {
          mImages.postValue(DataRepository.fetchPhotosFromQuery(searchQuery))
       }
    }



}