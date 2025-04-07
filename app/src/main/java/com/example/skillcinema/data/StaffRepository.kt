package com.example.skillcinema.data

import com.example.skillcinema.data.models.StaffDto
import javax.inject.Inject

class StaffRepository @Inject constructor(){

    suspend fun getStaff(filmId:Int):List<StaffDto>?{
        val response = RetrofitInstance.kinopoiskApi.getStaff(filmId)
        return if(response.isSuccessful){
            response.body()?: emptyList()
        } else {
            null
        }
    }
}