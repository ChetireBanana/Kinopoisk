package com.example.skillcinema.filmpage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.data.models.StaffList
import com.example.skillcinema.filmpage.domain.CollectFilmInfoUseCase
import com.example.skillcinema.gallerylistpage.data.ImagesCollection
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.localdatabase.entities.CollectionItemEntity
import com.example.skillcinema.localdatabase.entities.LocalCollectionEntity
import com.example.skillcinema.profilefragment.data.LocalDataBaseRepository
import com.example.skillcinema.seasonspage.data.SeasonsList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FilmPageViewModel @Inject constructor(
    private val collectFilmInfoUseCase: CollectFilmInfoUseCase,
    private val localDataBaseRepository: LocalDataBaseRepository
) : ViewModel() {

    private var mainDescription: FilmDto? = null
    private var seasonsListInfo: SeasonsList? = null
    private var actors: StaffList? = null
    private var staff: StaffList? = null
    private var images: ImagesCollection? = null
    private var similarFilms: FilmCollection? = null
    private var errors: MutableList<String> = mutableListOf()
    var webUrl: String? = null


    private val _mainDescriptionFlow = MutableStateFlow(mainDescription)
    val mainDescriptionFlow = _mainDescriptionFlow.asStateFlow()

    private val _seasonsInfoFlow = MutableStateFlow(seasonsListInfo)
    val seasonsInfoFlow = _seasonsInfoFlow.asStateFlow()

    private val _actorsFlow = MutableStateFlow(actors)
    val actorsFlow = _actorsFlow.asStateFlow()

    private val _staffFlow = MutableStateFlow(staff)
    val staffFlow = _staffFlow.asStateFlow()

    private val _imagesFlow = MutableStateFlow(images)
    val imagesFlow = _imagesFlow.asStateFlow()

    private val _similarFilmsFlow = MutableStateFlow(similarFilms)
    val similarFilmsFlow = _similarFilmsFlow.asStateFlow()

    private val _errorsFlow = MutableStateFlow<List<String>>(emptyList())
    val errorsFlow = _errorsFlow.asStateFlow()


    private val _pageState: MutableStateFlow<PageState> =
        MutableStateFlow(PageState.PageReady)
    val filmPgeState = _pageState.asStateFlow()

    init {
        if (mainDescription == null) {
            mainDescriptionObserver()
        }
        if (seasonsListInfo == null) {
            seasonsAndSeriesObserver()
        }
        if (actors == null) {
            actorsObserver()
        }
        if (staff == null) {
            staffObserver()
        }
        if (images == null) {
            imagesObserver()
        }
        if (similarFilms == null) {
            similarFilmsObserver()
        }
        errorsObserver()
    }


    fun loadFilmInfo(kinopoiskId: Int) {
        viewModelScope.launch {
            collectFilmInfoUseCase.execute(kinopoiskId)
        }
    }


    private fun mainDescriptionObserver() {
        viewModelScope.launch {
            _pageState.value = PageState.PageLoading
            collectFilmInfoUseCase.mainDescriptionFlow.collectLatest { filmWithMainDescription ->
                if (filmWithMainDescription != null) {
                    mainDescription = filmWithMainDescription
                    webUrl = filmWithMainDescription.webUrl
                    _mainDescriptionFlow.value = mainDescription
                    _pageState.value = PageState.PageReady
                    addFilmOnBd(filmWithMainDescription)
                    insertItemInLocalCollectionAndResetSize(
                        LocalBaseCollections.INTERESTING_ID,
                        filmWithMainDescription.kinopoiskId
                    )
                } else {
                    _pageState.value = PageState.PageError
                }
            }
        }
    }

    private fun seasonsAndSeriesObserver() {
        viewModelScope.launch {
            collectFilmInfoUseCase.seasonsInfoFlow.collectLatest { seasonsAndSeries ->
                if (seasonsAndSeries != null) {
                    seasonsListInfo = seasonsAndSeries
                    _seasonsInfoFlow.value = seasonsListInfo
                }
            }
        }
    }

    private fun actorsObserver() {
        viewModelScope.launch {
            collectFilmInfoUseCase.actorsFlow.collectLatest { actors ->
                if (actors != null) {
                    _actorsFlow.value = actors
                }
            }
        }
    }

    private fun staffObserver() {
        viewModelScope.launch {
            collectFilmInfoUseCase.staffFlow.collectLatest { staffInfo ->
                if (staffInfo != null) {
                    _staffFlow.value = staffInfo
                }
            }
        }
    }

    private fun imagesObserver() {
        viewModelScope.launch {
            collectFilmInfoUseCase.imagesFlow.collectLatest { imagesCollection ->
                if (imagesCollection != null) {
                    _imagesFlow.value = imagesCollection
                }
            }
        }
    }

    private fun similarFilmsObserver() {
        viewModelScope.launch {
            collectFilmInfoUseCase.similarFilmsFlow.collectLatest { similarFilms ->
                if (similarFilms != null) {
                    _similarFilmsFlow.value = similarFilms
                }
            }
        }
    }

    private fun errorsObserver() {
        viewModelScope.launch {
            collectFilmInfoUseCase.errorsFlow.collectLatest { error ->
                if (!error.isNullOrEmpty()) {
                    _errorsFlow.value = errors.toList()
                }
            }
        }
    }

    private fun addFilmOnBd(filmInfo: FilmDto) {
        viewModelScope.launch {
            localDataBaseRepository.adItemToDBFromFilm(filmInfo)
        }
    }

    fun insertItemInLocalCollectionAndResetSize(collectionId: Int, filmId: Int) {
        viewModelScope.launch {
            localDataBaseRepository.insertItemInLocalCollectionAndResetSize(collectionId, filmId)
        }
    }

    fun deleteItemFromLocalCollectionAndResetSize(collectionId: Int, filmId: Int) {
        viewModelScope.launch {
            localDataBaseRepository.deleteItemFromLocalCollectionAndResetSize(collectionId, filmId)
        }
    }

    fun checkIfViewed(id: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                localDataBaseRepository.checkIfItemIsInCollection(
                    id,
                    LocalBaseCollections.VIEWED_ID
                )
            }
            callback(result)
        }
    }

    fun getCollectionsFlowForItemId(id: Int): Flow<List<CollectionItemEntity>> {
        return localDataBaseRepository.getCollectionsFlowForItemId(id)
    }

    fun getAllCollectionsAvailable(): Flow<List<LocalCollectionEntity>> {
        return localDataBaseRepository.getAllCollectionsFromDB()
    }
}

