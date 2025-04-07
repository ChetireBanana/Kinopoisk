package com.example.skillcinema.data

import com.example.skillcinema.data.models.FilmDto
import javax.inject.Inject

class FilmInfoRepository @Inject constructor() {

    suspend fun getFilmDescription(kinopoiskId: Int): FilmDto? {
        val response = RetrofitInstance.kinopoiskApi.getFilmInfo(kinopoiskId)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}