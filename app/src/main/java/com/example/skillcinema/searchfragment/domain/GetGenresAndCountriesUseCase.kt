package com.example.skillcinema.searchfragment.domain

import android.content.Context
import android.util.Log
import com.example.skillcinema.R
import com.example.skillcinema.data.FiltersRepository
import com.example.skillcinema.data.models.FiltersGenresCountries
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class GetGenresAndCountriesUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val filtersGenresCountriesRepository: FiltersRepository
) {

    val errorList = mutableListOf<String>()

    private val _filtersFlow: MutableStateFlow<FiltersGenresCountries?> =
        MutableStateFlow(null)
    val filtersFlow = _filtersFlow.asStateFlow()

    private val _errorListFlow: MutableStateFlow<List<String>> =
        MutableStateFlow(errorList)
    val errorListFlow = _errorListFlow.asStateFlow()

    init{
        Log.d("SearchFragment", "SearchFilmsUseCase init")
    }

    suspend fun getGenresAndCountriesList(){
        Log.d("SearchFragment", "getGenresAndCountriesList init")
        val collectionMixed = filtersGenresCountriesRepository.getFilters()
        if (collectionMixed != null) {
            _filtersFlow.value = collectionMixed
        } else {
            errorList.add(
                context.getString(
                    R.string.movie_api_error,
                    context.getString(R.string.list_of_genre_and_countries_api_error)
                )
            )
            _errorListFlow.value = errorList.toList()
        }
    }


}