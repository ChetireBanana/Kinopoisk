package com.example.skillcinema.personalpage.domain

import android.content.Context
import com.example.skillcinema.R
import com.example.skillcinema.data.FilmInfoRepository
import com.example.skillcinema.data.PersonInfoRepository
import com.example.skillcinema.personalpage.data.PersonInfoDto
import com.example.skillcinema.data.models.PersonInfoFilmDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

open class CollectPersonInfoUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val personInfoRepository: PersonInfoRepository,
    private val filmInfoRepository: FilmInfoRepository
) {

    private val errorsList = mutableListOf<String>()

    private val _personInfoFlow: MutableStateFlow<PersonInfoDto?> = MutableStateFlow(null)
    open val personInfoFlow = _personInfoFlow.asStateFlow()

    private val _personInfoFilmsFlow: MutableStateFlow<List<PersonInfoFilmDto>?> =
        MutableStateFlow(null)
    val personInfoFilmsFlow = _personInfoFilmsFlow.asStateFlow()

    private val _errorFlow: MutableStateFlow<List<String>?> = MutableStateFlow(null)
    val errorFlow = _errorFlow.asStateFlow()


    private suspend fun collectPicturesForPersonInfoFilms(personInfoFilms: List<PersonInfoFilmDto>) {
        personInfoFilms.sortedByDescending { it.rating }
        if(personInfoFilms.size > 10){
            personInfoFilms.take(10)
        }
        personInfoFilms.forEach {
            val description = filmInfoRepository.getFilmDescription(it.filmId)
            if (description != null) {
                it.posterUrl = description.posterUrl
                it.posterUrlPreview = description.posterUrlPreview
            }
        }
        _personInfoFilmsFlow.value = personInfoFilms
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    suspend fun collectPersonInfo(personId: Int) {
        val info = personInfoRepository.getPersonInfo(personId)
        if (info != null) {
            _personInfoFlow.value = info
//            if (!info.films.isNullOrEmpty()) {
//                scope.launch {
//                    collectPicturesForPersonInfoFilms(info.films!!)
//                }
//            }
            _personInfoFlow.value = info
        } else {
            errorsList.add(context.getString(
                R.string.movie_api_error,
                context.getString(R.string.get_person_info_api_error)
            ))
            _errorFlow.value = errorsList.toList()
        }
    }
}