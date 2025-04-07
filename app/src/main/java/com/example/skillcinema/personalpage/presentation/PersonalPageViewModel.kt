package com.example.skillcinema.personalpage.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.data.models.PersonInfoFilmDto
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.personalpage.data.PersonInfoDto
import com.example.skillcinema.personalpage.domain.CollectPersonInfoUseCase
import com.example.skillcinema.profilefragment.data.LocalDataBaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class PersonalPageViewModel @Inject constructor(
    private val collectPersonInfoUseCase: CollectPersonInfoUseCase,
    private val localDataBaseRepository: LocalDataBaseRepository
) : ViewModel() {

    private var personInfo: PersonInfoDto? = null
    private var personInfoFilmography: List<PersonInfoFilmDto>? = null

    private val _personInfoFlow: MutableStateFlow<PersonInfoDto?> = MutableStateFlow(personInfo)
    val personInfoFlow = _personInfoFlow.asStateFlow()

    private val _personInfoFilmsFlow: MutableStateFlow<List<PersonInfoFilmDto>?> =
        MutableStateFlow(personInfoFilmography)
    val personInfoFilmsFlow = _personInfoFilmsFlow.asStateFlow()

    private val _personInfoErrorFlow: MutableStateFlow<List<String>?> =
        MutableStateFlow(null)
    val personInfoErrorFlow = _personInfoErrorFlow.asStateFlow()


    private val _personalPageStateFlow: MutableStateFlow<PageState> =
        MutableStateFlow(PageState.PageReady)
    open val personalPageStateFlow = _personalPageStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            personInfoObserver()
            personInfoFilmsObserver()
            errorObserver()
        }
    }


    open fun getPersonInfo(personId: Int) {
        if (personInfo == null || personInfoFilmography == null) {
            viewModelScope.launch {
                collectPersonInfoUseCase.collectPersonInfo(personId)
            }
        }
    }


    private fun personInfoObserver() {
        Log.d("personInfoObserver", "init")
        viewModelScope.launch {
            _personalPageStateFlow.value = PageState.PageLoading
            collectPersonInfoUseCase.personInfoFlow.collect {
                if (it != null) {
                    Log.d("PersonInfo", "${it.personId}")
                    personInfo = it
                    _personInfoFlow.value = it
                    _personalPageStateFlow.value = PageState.PageReady
                    addPersonOnBd(it)
                    addPersonInCollection(it.personId)
                }
            }
        }
    }

    private fun personInfoFilmsObserver() {
        viewModelScope.launch {
            collectPersonInfoUseCase.personInfoFilmsFlow.collectLatest { personInfoFilms ->
                if (personInfoFilms != null) {
                    personInfoFilmography = personInfoFilms
                    _personInfoFilmsFlow.value = personInfoFilms
                }
            }
        }
    }

    private fun errorObserver() {
        viewModelScope.launch {
            collectPersonInfoUseCase.errorFlow.collect { error ->
                if (error != null) {
                    _personInfoErrorFlow.value = error
                    _personalPageStateFlow.value = PageState.PageError
                }
            }
        }
    }

    private fun addPersonOnBd(personInfoDto: PersonInfoDto) {
        viewModelScope.launch {
            localDataBaseRepository.adItemToDBFromPerson(
                personInfoDto
            )
        }
    }

    private fun addPersonInCollection(personId: Int) {
        viewModelScope.launch {
            localDataBaseRepository.insertItemInLocalCollectionAndResetSize(LocalBaseCollections.INTERESTING_ID, personId)
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
