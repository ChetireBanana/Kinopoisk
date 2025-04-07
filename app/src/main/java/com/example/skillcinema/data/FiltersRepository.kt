package com.example.skillcinema.data

import android.util.Log
import com.example.skillcinema.data.models.FiltersGenresCountries
import javax.inject.Inject

class FiltersRepository @Inject constructor(){

    suspend fun getFilters(): FiltersGenresCountries? {
        Log.d("SearchFragment", "FiltersRepository init")
        val response = RetrofitInstance.kinopoiskApi.getFilters()
        return if (response.isSuccessful) {
            val responseBody = response.body()
            responseBody
        }else{
            null
        }
    }
}
