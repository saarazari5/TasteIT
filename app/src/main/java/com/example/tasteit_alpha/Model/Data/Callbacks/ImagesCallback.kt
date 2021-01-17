package com.example.tasteit_alpha.Model.Data.Callbacks

import com.example.tasteit_alpha.Model.Data.DataClasses.PresentAble

interface ImagesCallback {
    fun onResponse(imagesData: List<PresentAble>?, exc: Exception?)
}