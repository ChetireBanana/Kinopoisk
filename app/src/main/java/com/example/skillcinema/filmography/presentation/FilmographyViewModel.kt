package com.example.skillcinema.filmography.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.data.models.PersonInfoFilmDto
import com.example.skillcinema.filmography.domain.CreateFilmographyListUseCase
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.profilefragment.data.LocalDataBaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FilmographyViewModel @Inject constructor(
    private val createFilmographyListUseCase: CreateFilmographyListUseCase,
    private val localDataBaseRepository: LocalDataBaseRepository
) : ViewModel() {

    private var mainFilmography: List<PersonInfoFilmDto> = emptyList()
    private var additionalFilmography: List<PersonInfoFilmDto> = emptyList()
    private var actorsName: String? = null

    private val _filmographyPageStateFlow: MutableStateFlow<PageState> =
        MutableStateFlow(PageState.PageReady)
    val filmographyPageStateFlow = _filmographyPageStateFlow.asStateFlow()

    private val _mainFilmographyFlow: MutableStateFlow<List<PersonInfoFilmDto>> =
        MutableStateFlow(mainFilmography)
    val mainFilmographyFlow = _mainFilmographyFlow.asStateFlow()

    private val _additionalFilmographyFlow: MutableStateFlow<List<PersonInfoFilmDto>> =
        MutableStateFlow(mainFilmography)
    val additionalFilmographyFlow = _additionalFilmographyFlow.asStateFlow()

    private val _filmographyErrorFlow: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList())
    val filmographyErrorFlow = _filmographyErrorFlow.asStateFlow()

    private val _actorNameFlow: MutableStateFlow<String?> =
        MutableStateFlow(null)
    val actorNameFlow = _actorNameFlow.asStateFlow()

    init {
        errorObserver()
    }

    suspend fun getFilmography(personId: Int) {
        if (mainFilmography.isEmpty()) {
            _filmographyPageStateFlow.value = PageState.PageLoading
            createFilmographyListUseCase.getMainFilmography(personId)
            mainFilmographyObserver()
        }
        // Данный код вызывает множество обращений к БД расходует лимит
//        else {
//            if(additionalFilmography.isEmpty()) {
//                createFilmographyListUseCase.getAdditionalFilmographyInformation(mainFilmography)
//                additionalFilmographyObserver()
//            }
//        }
    }

    private fun mainFilmographyObserver() {
        viewModelScope.launch {
            createFilmographyListUseCase.mainFilmographyFlow.collectLatest { mainList ->
                if (mainList.isNotEmpty()) {
                    mainFilmography = mainList
                    _mainFilmographyFlow.value = mainFilmography
                    _filmographyPageStateFlow.value = PageState.PageReady
                } else {
                    _filmographyPageStateFlow.value = PageState.PageError
                }
            }
        }
    }

    private fun additionalFilmographyObserver() {
        viewModelScope.launch {
            createFilmographyListUseCase.additionalFilmographyFlow.collectLatest { additionalList ->
                if (additionalList.isNotEmpty()) {
                    additionalFilmography = additionalList
                    _additionalFilmographyFlow.value = additionalFilmography
                }
            }
        }
    }

    fun getActorName() {
        if(actorsName == null){
            viewModelScope.launch {
                createFilmographyListUseCase.personNameFlow.collect {
                    actorsName = it
                    _actorNameFlow.value = actorsName
                }
            }
        } else {
            _actorNameFlow.value = actorsName
        }

    }

    private fun errorObserver() {
        viewModelScope.launch {
            createFilmographyListUseCase.filmographyErrorFlow.collect { errorList ->
                if (errorList.isNotEmpty()) {
                    _filmographyErrorFlow.value = errorList.toList()
                }
            }
        }
    }

    fun checkIfViewed(itemId: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                localDataBaseRepository.checkIfItemIsInCollection(
                    itemId,
                    LocalBaseCollections.VIEWED_ID
                )
            }
            callback(result)
        }
    }
}