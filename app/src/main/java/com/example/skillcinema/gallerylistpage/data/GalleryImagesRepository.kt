package com.example.skillcinema.gallerylistpage.data

import android.util.Log
import com.example.skillcinema.data.RetrofitInstance
import javax.inject.Inject

class GalleryImagesRepository @Inject constructor(
) {

    suspend fun getFilmImagesCollection(
        kinopoiskId: Int,
        imagesType: String,
        page: Int
    ): ImagesCollection? {
        val response = RetrofitInstance.kinopoiskApi.getFilmImages(kinopoiskId, imagesType, page)
        return if (response.isSuccessful) {
            if (response.body() != null) {
                if(response.body()!!.total != 0) {
                    response.body()?.type = imagesType
                    Log.d("GalleryImagesRepository","${response.body()}")
                    response.body()
                } else {
                    null
                }
            } else {
                null
            }
        } else {
            null
        }
    }
}