package com.example.skillcinema.seasonspage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.seasonspage.data.SeasonsList
import com.example.skillcinema.seasonspage.domain.CollectSeasonsListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SeasonsPageViewModel @Inject constructor(
    private val collectSeasonsListUseCase: CollectSeasonsListUseCase
) : ViewModel() {

    private var seasonsList: SeasonsList? = null


    private val _pageState: MutableStateFlow<PageState> =
        MutableStateFlow(PageState.PageReady)
    val pageState = _pageState.asStateFlow()

    private val _seasonsListFlow: MutableStateFlow<SeasonsList?> =
        MutableStateFlow(seasonsList)
    val seasonsListFlow = _seasonsListFlow.asStateFlow()

    private val _errorFlow: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList())
    val errorFlow = _errorFlow.asStateFlow()

    init {
        errorObserver()
    }


    fun collectSeasonsList(kinopoiskId: Int) {
        if (seasonsList == null) {
            viewModelScope.launch {
                _pageState.value = PageState.PageLoading
                collectSeasonsListUseCase.execute(kinopoiskId)
                collectSeasonsListUseCase.seasonsListFlow.collect { newSeasonsList ->
                    if (newSeasonsList != null) {
                        seasonsList = newSeasonsList
                        _seasonsListFlow.value = newSeasonsList
                        _pageState.value = PageState.PageReady
                    } else {
                        _pageState.value = PageState.PageError
                    }

                }
            }
        }
    }

    private fun errorObserver() {
        viewModelScope.launch {
            collectSeasonsListUseCase.seasonsErrorFlow.collect { errorList ->
                if (errorList.isNotEmpty()) {
                    _errorFlow.value = errorList.toList()
                }
            }
        }
    }

}
