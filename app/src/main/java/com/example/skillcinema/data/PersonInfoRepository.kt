package com.example.skillcinema.data

import com.example.skillcinema.personalpage.data.PersonInfoDto
import javax.inject.Inject

class PersonInfoRepository @Inject constructor() {

    suspend fun getPersonInfo(personId: Int): PersonInfoDto? {
        val response = RetrofitInstance.kinopoiskApi.getPersonInfo(personId)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}