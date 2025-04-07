package com.example.skillcinema.searchfragment.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.data.models.Country
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.data.models.Genre
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.profilefragment.data.LocalDataBaseRepository
import com.example.skillcinema.searchfragment.data.FilterOptionsRepository
import com.example.skillcinema.searchfragment.domain.GetGenresAndCountriesUseCase
import com.example.skillcinema.searchfragment.domain.SearchResultsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class SearchFragmentViewModel @Inject constructor(
    private val getGenresAndCountriesUseCase: GetGenresAndCountriesUseCase,
    private val searchResultsUseCase: SearchResultsUseCase,
    private val localDataBaseRepository: LocalDataBaseRepository
) : ViewModel() {

    var keyword: String? = null
    private var filterOptionsInUse: FilterOptionsRepository = FilterOptionsRepository()
    var filterOptionsSaved: FilterOptionsRepository = FilterOptionsRepository()

    private var possibleCountries: List<Country> = listOf()
    private var possibleGenres: List<Genre> = listOf()

    private val _stateFlow: MutableStateFlow<PageState> =
        MutableStateFlow(PageState.PageReady)
    val stateFlow = _stateFlow.asStateFlow()

    private val _genresFlow: MutableStateFlow<List<Genre>> =
        MutableStateFlow(possibleGenres)
    val genresFlow = _genresFlow.asStateFlow()

    private val _countriesFlow: MutableStateFlow<List<Country>> =
        MutableStateFlow(possibleCountries)
    val countriesFlow = _countriesFlow.asStateFlow()

    private val _errorListFlow: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList())
    val errorListFlow = _errorListFlow.asStateFlow()


    init {
        Log.d("SearchFragment", "ViewModel init")
        errorsObserver()
    }

    fun getSearchResultsPagedCollection(): Flow<PagingData<FilmDto>> {
            return searchResultsUseCase.getSearchResultsPagedCollection(getFiltersMap(),filterOptionsInUse.filterNoViewed)
                .cachedIn(viewModelScope)
    }

    fun getGenresAndCountriesLists() {
        if (possibleCountries.isEmpty() || possibleGenres.isEmpty()) {
            Log.d("SearchFragment", "getGenresAndCountriesLists init")
            viewModelScope.launch {
                _stateFlow.value = PageState.PageLoading
                getGenresAndCountriesUseCase.getGenresAndCountriesList()
                getGenresAndCountriesUseCase.filtersFlow.collect { filters ->
                    if (filters != null) {
                        possibleGenres = filters.genres
                        possibleGenres.forEach {
                            it.genre = it.genre.replaceFirstChar { firstChar ->
                                if (firstChar.isLowerCase()) firstChar.titlecase(
                                    Locale.getDefault()
                                ) else firstChar.toString()
                            }
                        }
                        _genresFlow.value = filters.genres.toList()
                        Log.d("getGenresAndCountriesLists", "${possibleGenres.size}")

                        possibleCountries = filters.countries
                        _countriesFlow.value = filters.countries.toList()

                        _stateFlow.value = PageState.PageReady
                    } else {
                        _stateFlow.value = PageState.PageError
                    }
                }
            }
        }
    }

    private fun getFiltersMap(): HashMap<String, String?> {
        val filtersMap = hashMapOf<String, String?>().apply {
            if (!keyword.isNullOrBlank()) put("keyword", keyword)
            if (filterOptionsInUse.countries?.id != null) put(
                "countries",
                filterOptionsInUse.countries?.id.toString()
            )
            if (filterOptionsInUse.genres?.id != null) put(
                "genres",
                filterOptionsInUse.genres?.id.toString()
            )
            put("order", filterOptionsInUse.order)
            put("type", filterOptionsInUse.type)
            put("ratingFrom", filterOptionsInUse.ratingFrom.toString())
            put("ratingTo", filterOptionsInUse.ratingTo.toString())
            if (filterOptionsInUse.yearFrom != null) put(
                "yearFrom",
                filterOptionsInUse.yearFrom.toString()
            )
            if (filterOptionsInUse.yearTo != null) put(
                "yearTo",
                filterOptionsInUse.yearTo.toString()
            )
        }

        Log.d("SearchFragment", "getFiltersMap $filtersMap")

        return filtersMap
    }

    private fun errorsObserver() {
        viewModelScope.launch {
            getGenresAndCountriesUseCase.errorListFlow.collect { errors ->
                if (errors.isNotEmpty()) {
                    _errorListFlow.value = errors.toList()
                }
            }
        }
    }

    fun resetFilters() {
        filterOptionsSaved = FilterOptionsRepository()
        Log.d("SearchFragment", "resetFilters")
    }

    fun isSearchPreferencesSettingsDefault(): Boolean {
        val isFilterOptionsSavedDefault = filterOptionsSaved == FilterOptionsRepository()
        Log.d("SearchFragment", "isSearchPreferencesSettingsDefault $isFilterOptionsSavedDefault")
        return isFilterOptionsSavedDefault
    }

    fun setSavedOptionsInUse() {
        filterOptionsInUse = filterOptionsSaved
        Log.d("SearchFragment", "setSavedOptionsInUse")
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
}