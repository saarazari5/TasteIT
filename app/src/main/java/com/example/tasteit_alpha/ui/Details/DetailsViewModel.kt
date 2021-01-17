package com.example.tasteit_alpha.ui.Details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasteit_alpha.Model.DataRepository
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.PlaceDetails
import kotlinx.coroutines.launch

class DetailsViewModel () : ViewModel() {
    val mPlaceDetails : MutableLiveData<PlaceDetails> = MutableLiveData()
    //launch the fetch data method in here and bind it to viewModel lifeCycle
        fun fetchData(address:String){
        viewModelScope.launch {
            mPlaceDetails.postValue(DataRepository.fetchDetails(address))
        }
   }
}