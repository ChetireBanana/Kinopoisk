package com.example.skillcinema.filmpage.domain

import android.content.Context
import android.util.Log
import com.example.skillcinema.R
import com.example.skillcinema.filmpage.data.FilmPageImagesRepository
import com.example.skillcinema.data.FilmInfoRepository
import com.example.skillcinema.seasonspage.data.FilmSeasonsRepository
import com.example.skillcinema.data.SimilarFilmsCollectionRepository
import com.example.skillcinema.data.StaffRepository
import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.gallerylistpage.data.ImagesCollection
import com.example.skillcinema.seasonspage.data.SeasonsList
import com.example.skillcinema.data.models.StaffList
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CollectFilmInfoUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val filmInfoRepository: FilmInfoRepository,
    private val filmSeasonsRepository: FilmSeasonsRepository,
    private val filmPageImagesRepository: FilmPageImagesRepository,
    private val staffRepository: StaffRepository,
    private val similarFilmsRepository: SimilarFilmsCollectionRepository
) {

    private val errorList = mutableListOf<String>()

    private val _mainDescriptionFlow = MutableStateFlow<FilmDto?>(null)
    val mainDescriptionFlow = _mainDescriptionFlow.asStateFlow()

    private val _seasonsListInfoFlow = MutableStateFlow<SeasonsList?>(null)
    val seasonsInfoFlow = _seasonsListInfoFlow.asStateFlow()

    private val _actorsFlow = MutableStateFlow<StaffList?>(null)
    val actorsFlow = _actorsFlow.asStateFlow()

    private val _staffFlow = MutableStateFlow<StaffList?>(null)
    val staffFlow = _staffFlow.asStateFlow()

    private val _imagesFlow = MutableStateFlow<ImagesCollection?>(null)
    val imagesFlow = _imagesFlow.asStateFlow()

    private val _similarFilmsFlow = MutableStateFlow<FilmCollection?>(null)
    val similarFilmsFlow = _similarFilmsFlow.asStateFlow()

    private val _errorsFlow = MutableStateFlow<List<String>?>(errorList)
    val errorsFlow = _errorsFlow.asStateFlow()

    private val parentJob = Job()
    private val scope = CoroutineScope(parentJob + Dispatchers.IO)

    suspend fun execute(kinopoiskId: Int) {

        Log.d("CollectFilmInfoUseCase", kinopoiskId.toString())
        scope.launch {
            getDescription(kinopoiskId)
            Log.d("CollectFilmInfoUseCase ", "getDescription")
        }
        scope.launch {
            getStaff(kinopoiskId)
            Log.d("CollectFilmInfoUseCase ", "getStaff")
        }
        scope.launch {
            getImages(kinopoiskId)
            Log.d("CollectFilmInfoUseCase ", "getImages")
        }

        scope.launch {
            getSimilarFilmsCollection(kinopoiskId)
            Log.d("CollectFilmInfoUseCase ", "getSimilarFilms")
        }

        parentJob.complete()
        parentJob.join()
    }



    private suspend fun getDescription(kinopoiskId: Int) {
        val description = filmInfoRepository.getFilmDescription(kinopoiskId)
        if (description != null) {
            _mainDescriptionFlow.value = description
            if (description.serial) {
                scope.launch {
                    getSeasonsInfo(kinopoiskId)
                }
            }
        } else {
            errorList.add(context.getString(R.string.movie_api_error,
                context.getString(R.string.movie_description_error_add)))
            _errorsFlow.value = errorList
        }
    }

    private suspend fun getSeasonsInfo(kinopoiskId: Int) {
        delay(1000)
        val seasonsInfo = filmSeasonsRepository.getFilmSeasonsInfo(kinopoiskId)
        if (seasonsInfo != null) {
            _seasonsListInfoFlow.value = seasonsInfo
        } else {
            errorList.add( context.getString(R.string.movie_api_error,
                context.getString(R.string.season_information_error_add)))
            _errorsFlow.value = errorList
        }

    }

    private suspend fun getStaff(kinopoiskId: Int) {
        val staff = staffRepository.getStaff(kinopoiskId)
        if (staff != null) {
            val actorsList = StaffList(
                title = context.getString(R.string.actors_list_title),
                items = staff.filter { it.professionKey == "ACTOR" } )
            val creatorsList = StaffList(
                title = context.getString(R.string.crew_list_title),
                items = staff.filterNot { it.professionKey == "ACTOR" }
            )
            _actorsFlow.value = actorsList
            _staffFlow.value = creatorsList
        } else {
            errorList.add(context.getString(R.string.movie_api_error,
                context.getString(R.string.crew_api_error)))
            _errorsFlow.value = errorList
        }
    }

    private suspend fun getImages(kinopoiskId: Int) {
        val imageCollection = filmPageImagesRepository.getFilmPageImagesCollection(kinopoiskId)
        if (imageCollection.items.isNotEmpty()) {
            _imagesFlow.value = imageCollection
        } else {
            errorList.add( context.getString(R.string.movie_api_error,
                context.getString(R.string.title_gallery_layout)))
            _errorsFlow.value = errorList
        }
    }

    private suspend fun getSimilarFilmsCollection(kinopoiskId: Int) {
        val similarFilmCollection = similarFilmsRepository.getSimilarFilms(kinopoiskId)
        if (similarFilmCollection != null) {
            Log.d("similarFilms", "use case ${similarFilmCollection.items.size}")
            _similarFilmsFlow.value = similarFilmCollection
        } else {
            errorList.add(context.getString(R.string.movie_api_error,
                context.getString(R.string.similar_films_list_title)))
            _errorsFlow.value = errorList
        }
    }
}
