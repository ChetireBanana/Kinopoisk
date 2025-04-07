package com.example.skillcinema.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.data.models.extras.FilmCollectionTags
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class FilmCollectionsRepository @Inject constructor(
    private val dateRepository: DateRepository
) {

    init {
        Log.d("SearchFragment", "FilmCollectionsRepository init")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getPremiers(title: String): FilmCollection? {
        val months = dateRepository.getPremierMonth()
        val year = dateRepository.getPremierYear()
        val formatter = dateRepository.getFormatter()
        val todayDate = dateRepository.getTodayDate()

        val premiers = FilmCollection(
            total = 0,
            title = title,
            items = mutableListOf(),
            tag = FilmCollectionTags.SIMPLE_COLLECTION
        )
        months.forEach { month ->
            val response = RetrofitInstance.kinopoiskApi.getPremiers(year, month)
            if (response.isSuccessful) {
                premiers.items += (response.body()?.items ?: emptyList())
            }
        }
        premiers.items.filter {
            ChronoUnit.DAYS.between(
                LocalDate.parse(todayDate, formatter),
                LocalDate.parse(it.premiereRu, formatter)
            ) <= 14
        }
        premiers.total = premiers.items.size
        return if (premiers.items.isNotEmpty()) {
            premiers
        } else {
            null
        }
    }

    suspend fun getTopListFilmCollection(
        title: String,
        filters: Map<String, String?>,
        page: Int = 1,
    ): FilmCollection? {
        Log.d("SearchFragment", "getTopListFilmCollection init")
        val topList = FilmCollection(
            total = 0,
            title = title,
            items = listOf(),
            filters = filters,
            tag = FilmCollectionTags.TOP_LIST_PAGED_COLLECTION
        )
        val response = RetrofitInstance.kinopoiskApi.getTopListFilmCollection(
            filters = filters as HashMap<String, String?>, page
        )
        if (response.isSuccessful) {
            topList.total = response.body()?.total ?: 0
            topList.items = response.body()?.items ?: emptyList()
        }
        return if (topList.items.isNotEmpty()) {
            topList
        } else {
            null
        }
    }

    suspend fun getFilmsAndSerialsByFilters(
        title: String,
        filters: Map<String, String?>,
        page: Int = 1,
    ): FilmCollection? {
        Log.d("SearchFragment", "getFilmsAndSerialsByFilters init")
        val filmsAndSerialsByFilters = FilmCollection(
            total = 0,
            title = title,
            items = listOf(),
            filters = filters,
            tag = FilmCollectionTags.FILM_AND_SERIALS_BY_FILTERS_PAGED_COLLECTION
        )
        val response = RetrofitInstance.kinopoiskApi.getFilmsAndSerialsByFilters(
            filters = filters as HashMap<String, String?>,
            page
        )
        if (response.isSuccessful) {
            filmsAndSerialsByFilters.total = response.body()?.total ?: 0
            filmsAndSerialsByFilters.items = response.body()?.items ?: emptyList()
        }
        return if (filmsAndSerialsByFilters.items.isNotEmpty()) {
            filmsAndSerialsByFilters
        } else {
            null
        }
    }
}