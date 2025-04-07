package com.example.skillcinema.homepage.presentation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.homepage.domain.HomepageListOfFilmCollectionsUseCase
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.profilefragment.data.LocalDBRepository
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
open class HomepageViewModel @Inject constructor(
    private val homepageListOfFilmCollectionsUseCase: HomepageListOfFilmCollectionsUseCase,
    private val localDataBaseRepository: LocalDBRepository
) : ViewModel() {

    private var listOfFilmCollections: List<FilmCollection> = emptyList()
    private var errorList: List<String> = emptyList()


    private val _pageState: MutableStateFlow<PageState> =
        MutableStateFlow(PageState.PageReady)
    val pageState = _pageState.asStateFlow()

    private val _filmCollectionsFlow: MutableStateFlow<List<FilmCollection>> =
        MutableStateFlow(listOfFilmCollections)
    val filmCollectionsFlow = _filmCollectionsFlow.asStateFlow()

    private val _errorsFlow: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList())
    val errorsFlow = _errorsFlow.asStateFlow()


    init {
        creatingFilmCollectionsErrorsObserver()

        if (listOfFilmCollections.isEmpty()) {
            createListOfFilmCollections()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createListOfFilmCollections() {
        viewModelScope.launch {
            _pageState.value = PageState.PageLoading
            homepageListOfFilmCollectionsUseCase.execute()
            homepageListOfFilmCollectionsUseCase.filmCollectionsFlow.collect {
                if (it.isNotEmpty()) {
                    listOfFilmCollections = it
                    _filmCollectionsFlow.value = it
                    _pageState.value = PageState.PageReady
                }
            }
        }
    }

    private fun creatingFilmCollectionsErrorsObserver() {
        viewModelScope.launch {
            homepageListOfFilmCollectionsUseCase.errorsFlow
                .collect { errorsCollected ->
                    if (errorsCollected.isNotEmpty()) {

                        if (errorList != errorsCollected.toList()) {
                            Log.d("ErrorViewModel", "Список ошибок не пустой!")
                            println("списки не равны, обновляем!")
                            errorList = errorsCollected
                            _errorsFlow.value = errorsCollected
                        }
                        if (errorsCollected.size > 3) {
                            Log.d("ErrorViewModel", "Список ошибок переполнен!")
                            _pageState.value = PageState.PageError
                        }
                    }
                }
        }
    }

    fun checkIfViewed(
        id: Int,
        callback: (Boolean) -> Unit,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch {
            val result = withContext(dispatcher) {
                localDataBaseRepository.checkIfItemIsInCollection(
                    id,
                    LocalBaseCollections.VIEWED_ID
                )
            }
            callback(result)
        }
    }

    fun clearErrorsWhenLeaveScreen() {
        _errorsFlow.value = emptyList()
    }

    fun resetState() {
        _pageState.value = PageState.PageLoading
        _errorsFlow.value = emptyList()
    }


}