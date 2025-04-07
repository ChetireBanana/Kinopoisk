package com.example.skillcinema.data

import android.content.Context
import android.util.Log
import com.example.skillcinema.R
import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.data.models.SimilarFilmsCollection
import com.example.skillcinema.data.models.extras.FilmCollectionTags
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SimilarFilmsCollectionRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun getSimilarFilms(kinopoiskId: Int): FilmCollection? {
        val response = RetrofitInstance.kinopoiskApi.getSimilarFilms(kinopoiskId)
        Log.d("similarFilms", "Repository response${response.isSuccessful}")
        return if (response.isSuccessful) {
            if (response.body()?.items?.isNotEmpty() == true) {
                Log.d("similarFilms", "Repository getSimilarFilms ${response.body()!!.items.size}")
                convertToFilmCollection(response.body()!!)
            } else {
                null
            }
        } else {
            null
        }
    }

    private fun convertToFilmCollection(response: SimilarFilmsCollection): FilmCollection {
        val convertedFilmCollection = FilmCollection(0, items =  emptyList())
        if (response.total != 0) {
            convertedFilmCollection.total = response.total
            convertedFilmCollection.title = context.getString(R.string.similar_films_list_title)
            convertedFilmCollection.tag = FilmCollectionTags.SIMPLE_COLLECTION
            val convertedItemsList = mutableListOf<FilmDto>()
            response.items.forEach {
                val convertedFilm = FilmDto(
                    kinopoiskId = it.filmId,
                    nameRu = it.nameRu,
                    nameOriginal = it.nameOriginal,
                    posterUrl = it.posterUrl,
                    posterUrlPreview = it.posterUrlPreview
                )
                convertedItemsList.add(convertedFilm)
            }
            convertedFilmCollection.items = convertedItemsList.toList()
            Log.d("similarFilms", "Repository convertToFilmCollection ${convertedItemsList.size}")

        }
        return convertedFilmCollection


    }
}