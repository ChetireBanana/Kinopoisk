package com.example.skillcinema.filmpage.data

import com.example.skillcinema.data.RetrofitInstance
import com.example.skillcinema.gallerylistpage.data.Images
import com.example.skillcinema.gallerylistpage.data.ImagesCollection
import com.example.skillcinema.application.presentation.getPossibleImagesTypesList
import javax.inject.Inject

class FilmPageImagesRepository @Inject constructor() {

    private val possibleTypeOfImages = getPossibleImagesTypesList()


    suspend fun getFilmPageImagesCollection(kinopoiskId: Int): ImagesCollection {
        val items: MutableList<Images> = mutableListOf()

        run breaking@{
            possibleTypeOfImages.forEach { imageType ->
                if (items.size > 20) return@breaking
                val response =
                    RetrofitInstance.kinopoiskApi.getFilmImages(kinopoiskId, imageType, 1)
                if (response.isSuccessful) {
                    val images = response.body()
                    if (images != null) {
                        items.addAll(images.items)
                        val totalPages = images.totalPages
                        if (totalPages > 1) {
                            for (page in 2..totalPages) {
                                val subResponse = RetrofitInstance.kinopoiskApi.getFilmImages(
                                    kinopoiskId,
                                    imageType,
                                    page
                                )
                                if (subResponse.isSuccessful) {
                                    val subImages = response.body()
                                    if (subImages != null) {
                                        items.addAll(subImages.items)
                                    }
                                } else {
                                    return@breaking
                                }
                            }
                        }
                    }
                } else {
                    return@breaking
                }
            }
        }
        return ImagesCollection("FilmPageImagesCollection", 0, 1, items)
    }
}
