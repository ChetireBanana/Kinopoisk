package com.example.skillcinema.homepage.domain

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.skillcinema.R
import com.example.skillcinema.data.FilmCollectionsRepository
import com.example.skillcinema.data.FiltersRepository
import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.data.models.extras.FiltersTypesOfFilms
import com.example.skillcinema.data.models.extras.TopListTypes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface HomepageListOfFilmCollectionsUseCase {
    val filmCollectionsFlow: StateFlow<List<FilmCollection>>
    val errorsFlow: StateFlow<List<String>>
    fun execute()
}

@Suppress("UNCHECKED_CAST")
class CreateHomepageListOfFilmCollectionsUseCase @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val filmCollectionsRepository: FilmCollectionsRepository,
    private val filtersRepository: FiltersRepository
): HomepageListOfFilmCollectionsUseCase {

    private val _filmCollectionsFlow: MutableStateFlow<List<FilmCollection>> =
        MutableStateFlow(emptyList())
    override val filmCollectionsFlow = _filmCollectionsFlow.asStateFlow()

    private val _errorsFlow: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList())
    override val errorsFlow = _errorsFlow.asStateFlow()

    private var listOfFilmsCollection = mutableListOf<FilmCollection>()
    private val listOfErrors = mutableListOf<String>()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun execute() {
        scope.launch {
            Log.d("CreateHomepageListOfFilmCollectionsUseCase", "getPremiers")
            getPremiers()
        }
        scope.launch {
            Log.d("CreateHomepageListOfFilmCollectionsUseCase", "TOP_POPULAR_MOVIES")
            getFilmCollection(TopListTypes.TOP_POPULAR_MOVIES)
        }

        scope.launch {
            Log.d("CreateHomepageListOfFilmCollectionsUseCase", "TOP_250_MOVIES")
            getFilmCollection(TopListTypes.TOP_250_MOVIES)
        }

        scope.launch {
            Log.d("CreateHomepageListOfFilmCollectionsUseCase", "TV_SERIES")
            getPopularSerials(
                FiltersTypesOfFilms.TV_SERIES,
                ratingFrom = 8
            )
        }

        repeat(2){
            scope.launch {
                Log.d("CreateHomepageListOfFilmCollectionsUseCase", "RandomGenreAndCountryFilmCollection")
                getRandomGenreAndCountryFilmCollection()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getPremiers(
    ) {
        val title = context.getString(R.string.premiers_collection_title)

        val premiers = filmCollectionsRepository.getPremiers(title)
        if (premiers != null) {
            Log.d("getPremiers", "${premiers.items.size} ${premiers.total}")
            addCollectionIntoListAndSend(premiers)
        } else {
            addErrorToListAndSend(title)
        }
    }

    private suspend fun getFilmCollection(
        type: String,
        page: Int = 1
    ) {

        val title = when (type) {
            TopListTypes.TOP_250_MOVIES -> context.getString(
                R.string.top_250_movies_collection_title
            )

            TopListTypes.TOP_POPULAR_MOVIES -> context.getString(
                R.string.top_popular_movies_collection_title
            )

            else -> {
                "Unknown collection type"
            }
        }
        val filters: HashMap<String, String?> = hashMapOf(
            "type" to type
        )


        val topMovies = filmCollectionsRepository.getTopListFilmCollection(title, filters, page)


        if (topMovies != null) {
            addCollectionIntoListAndSend(topMovies)
        } else {
            addErrorToListAndSend(title)
        }
    }

    private suspend fun getPopularSerials(
        type: String? = null,
        ratingFrom: Int? = null,
    ) {
        val title = context.getString(R.string.popular_serials_title)

        val filters: HashMap<String, String?> = hashMapOf(
            "type" to type,
            "ratingFrom" to ratingFrom?.toString()
        ).filterNot { it.value == null } as HashMap<String, String?>

        val popularSerials = filmCollectionsRepository.getFilmsAndSerialsByFilters(
            title = title,
            filters = filters
        )

        if (popularSerials != null) {
            addCollectionIntoListAndSend(popularSerials)
        } else {
            addErrorToListAndSend(title)
        }
    }


    private suspend fun getRandomGenreAndCountryFilmCollection() {
        val listOfFilters = filtersRepository.getFilters()
        if (listOfFilters != null) {
            var isFilmCollectionEmpty = true
            while (isFilmCollectionEmpty) {
                val randomGenre = listOfFilters.genres.random()
                val randomCountry = listOfFilters.countries.random()
                Log.d("getFilters", "$randomGenre $randomCountry")

                val title = "${randomGenre.genre.replaceFirstChar { it.uppercase() }} ${randomCountry.country}"

                val filters: HashMap<String, String?> = hashMapOf(
                    "type" to FiltersTypesOfFilms.FILM,
                    "countries" to randomCountry.id,
                    "genres" to randomGenre.id
                ).filterNot { it.value == null } as HashMap<String, String?>

                val randomFilmCollection = filmCollectionsRepository.getFilmsAndSerialsByFilters(
                    title = title,
                    filters = filters
                )

                if (randomFilmCollection != null) {
                    if (randomFilmCollection.items.isNotEmpty()) {
                        isFilmCollectionEmpty = false
                        addCollectionIntoListAndSend(randomFilmCollection)
                    }
                } else {
                    listOfErrors.add(
                        context.getString(
                            R.string.movie_api_error,
                            "Cлучайную коллекцию"
                        )
                    )
                }
            }
        }
    }


    private fun addCollectionIntoListAndSend(filmCollection: FilmCollection) {
        if (filmCollection.title == context.getString(R.string.premiers_collection_title)) {
            listOfFilmsCollection.add(0, filmCollection)
        } else {
            listOfFilmsCollection.add(filmCollection)
        }
        _filmCollectionsFlow.value = listOfFilmsCollection.toList()
        Log.d("onSuccessCollection", "${_filmCollectionsFlow.value.size}")
    }

    private fun addErrorToListAndSend(message: String) {
        listOfErrors.add(context.getString(R.string.movie_api_error, message))
        Log.d("onFailure", "${listOfErrors.size}")
        _errorsFlow.value = listOfErrors.toList()
    }
}

