package com.example.skillcinema.seasonspage.domain

import android.content.Context
import com.example.skillcinema.R
import com.example.skillcinema.seasonspage.data.FilmSeasonsRepository
import com.example.skillcinema.seasonspage.data.SeasonsList
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class CollectSeasonsListUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val seasonsRepository: FilmSeasonsRepository
) {

    private val errorList: MutableList<String> = mutableListOf()

    private val _seasonsListFlow: MutableStateFlow<SeasonsList?> =
        MutableStateFlow(null)
    val seasonsListFlow = _seasonsListFlow.asStateFlow()

    private val _seasonsErrorFlow: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList())
    val seasonsErrorFlow = _seasonsErrorFlow.asStateFlow()


    suspend fun execute(kinopoiskId: Int) {
        val seasonsList = seasonsRepository.getFilmSeasonsInfo(kinopoiskId)
        if (seasonsList != null){
            _seasonsListFlow.value = seasonsList
        } else{
            errorList.add(context.getString(R.string.movie_api_error,
                context.getString(R.string.seasons_api_error)))
            _seasonsErrorFlow.value = errorList
        }
    }
}