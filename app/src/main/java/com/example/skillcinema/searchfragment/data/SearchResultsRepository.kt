package com.example.skillcinema.searchfragment.data

import android.util.Log
import com.example.skillcinema.data.RetrofitInstance
import com.example.skillcinema.data.models.FilmCollection

import javax.inject.Inject


class SearchResultsRepository @Inject constructor() {

    suspend fun getSearchResults(
        filters: Map<String, String?>,
        page: Int,
    ): FilmCollection? {
        Log.d("SearchFragment", "SearchResultsRepository init")
        val response = RetrofitInstance.kinopoiskApi.getFilmsAndSerialsByFilters(
            filters = filters as HashMap<String, String?>,
            page
        )
       return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}