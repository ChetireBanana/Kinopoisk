package com.example.skillcinema.filmography.domain

import android.content.Context
import com.example.skillcinema.R
import com.example.skillcinema.data.FilmInfoRepository
import com.example.skillcinema.data.PersonInfoRepository
import com.example.skillcinema.data.models.PersonInfoFilmDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class CreateFilmographyListUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val personInfoRepository: PersonInfoRepository,
    private val filmInfoRepository: FilmInfoRepository
) {

    private val errorList: MutableList<String> = mutableListOf()

    private val _mainFilmographyFlow: MutableStateFlow<List<PersonInfoFilmDto>> =
        MutableStateFlow(emptyList())
    val mainFilmographyFlow = _mainFilmographyFlow.asStateFlow()

    private val _additionalFilmographyFlow: MutableStateFlow<List<PersonInfoFilmDto>> =
        MutableStateFlow(emptyList())
    val additionalFilmographyFlow = _additionalFilmographyFlow.asStateFlow()

    private val _filmographyErrorFlow: MutableStateFlow<List<String>> =
        MutableStateFlow(errorList)
    val filmographyErrorFlow = _filmographyErrorFlow.asStateFlow()

    private val _personNameFlow: MutableStateFlow<String?> =
        MutableStateFlow(null)
    val personNameFlow = _personNameFlow.asStateFlow()




    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    suspend fun getMainFilmography(personId: Int) {
        val personInfo = personInfoRepository.getPersonInfo(personId)
        if (personInfo?.films != null) {
            _mainFilmographyFlow.value = personInfo.films!!
//            scope.launch {
//                getAdditionalFilmographyInformation(personInfo.films!!)
//            }
        } else {
            errorList.add(
                context.getString(
                    R.string.movie_api_error,
                    context.getString(R.string.filmography_api_error_message)
                )
            )
            _filmographyErrorFlow.value = errorList.toList()
        }

        if (personInfo?.nameEn != null || personInfo?.nameRu != null) {
            _personNameFlow.value = personInfo.nameRu ?: personInfo.nameEn
        } else {
            errorList.add(
                context.getString(
                    R.string.movie_api_error,
                    context.getString(R.string.filmography_cant_get_name_error_text)
                )
            )
            _filmographyErrorFlow.value = errorList.toList()
        }
    }

    suspend fun getAdditionalFilmographyInformation(filmography: List<PersonInfoFilmDto>) {
        run breaking@{
            filmography.forEach { film ->
                val additionalFilmInfo = filmInfoRepository.getFilmDescription(film.filmId)
                if (additionalFilmInfo != null) {
                    film.posterUrlPreview = additionalFilmInfo.posterUrlPreview
                    film.posterUrl = additionalFilmInfo.posterUrl
                    film.year = additionalFilmInfo.year
                    film.genre = additionalFilmInfo.genres[0].genre
                } else {
                    return@breaking
                }
            }
        }
        _additionalFilmographyFlow.value = filmography
    }


}
