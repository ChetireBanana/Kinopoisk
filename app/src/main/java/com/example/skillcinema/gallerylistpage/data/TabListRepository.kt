package com.example.skillcinema.gallerylistpage.data

import android.content.Context
import android.util.Log
import com.example.skillcinema.R
import com.example.skillcinema.application.presentation.getPossibleImagesTypesList
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class TabListRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val galleryImagesRepository: GalleryImagesRepository

){

    private val possibleTypeOfImages = getPossibleImagesTypesList()

    private val errorList = mutableListOf<String>()

    private val _tabListFlow: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val tabListFlow = _tabListFlow.asStateFlow()

    private val _errorsListFlow: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val errorsListFlow = _errorsListFlow.asStateFlow()

    suspend fun getTabList(kinopoiskId: Int) {
        val tabList = mutableListOf<String>()

        possibleTypeOfImages.forEach { typeOfImage ->
            val imageType = galleryImagesRepository.getFilmImagesCollection(kinopoiskId, typeOfImage, 1)
            if (imageType != null) {
                Log.d("TabListRepository getTabList", typeOfImage)
                tabList.add(typeOfImage)

            }
        }
        if (tabList.isNotEmpty()) {
            Log.d("TabListRepository", tabList.toString())
            _tabListFlow.value = tabList
        } else {
            errorList.add(
                context.getString(
                    R.string.movie_api_error,
                    context.getString(R.string.gallery_error)
                )
            )
            _errorsListFlow.value = errorList.toList()
        }


    }
}