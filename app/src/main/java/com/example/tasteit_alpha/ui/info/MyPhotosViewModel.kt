package com.example.tasteit_alpha.ui.info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasteit_alpha.Model.Data.DataClasses.ImageDatum
import com.example.tasteit_alpha.Model.Data.DataClasses.PresentAble
import com.example.tasteit_alpha.Model.DataRepository
import kotlinx.coroutines.launch

class MyPhotosViewModel : ViewModel() {
    val mImages: MutableLiveData<List<String>?> = MutableLiveData()

    init {
        viewModelScope.launch {
            mImages.postValue(DataRepository.fetchMyPhotos())
        }
    }
}