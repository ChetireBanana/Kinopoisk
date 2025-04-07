package com.example.skillcinema.seasonspage.data

import com.example.skillcinema.data.RetrofitInstance.kinopoiskApi
import javax.inject.Inject

class FilmSeasonsRepository @Inject constructor() {

    suspend fun getFilmSeasonsInfo(kinopoiskId: Int): SeasonsList? {
        val response = kinopoiskApi.getSeasonsInfo(kinopoiskId)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}