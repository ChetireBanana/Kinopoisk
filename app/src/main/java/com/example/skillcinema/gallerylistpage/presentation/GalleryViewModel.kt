package com.example.skillcinema.gallerylistpage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.gallerylistpage.data.GalleryImagesRepository
import com.example.skillcinema.gallerylistpage.data.Images
import com.example.skillcinema.gallerylistpage.data.ImagesCollectionPagingSource
import com.example.skillcinema.gallerylistpage.data.TabListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GalleryViewModel @Inject constructor(
    private val galleryImagesRepository: GalleryImagesRepository,
    private val tabListRepository: TabListRepository
) : ViewModel() {

    private var tabList = listOf<String>()

    private val _tabListFlow: MutableStateFlow<List<String>> = MutableStateFlow(tabList)
    val tabListFlow = _tabListFlow.asStateFlow()

    private val _galleryPageState: MutableStateFlow<PageState> =
        MutableStateFlow(PageState.PageReady)
    val galleryPageState = _galleryPageState.asStateFlow()

    private val _galleryErrorFlow: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList())
    val galleryErrorFlow = _galleryErrorFlow.asStateFlow()

    init{
        errorObserver()
    }

    fun getTabList(kinopoiskId: Int) {
        if (tabList.isEmpty()) {
            viewModelScope.launch {
                _galleryPageState.value = PageState.PageLoading
                tabListRepository.getTabList(kinopoiskId)

                tabListRepository.tabListFlow.collect { newTabList ->

                    if (newTabList.isNotEmpty()) {
                        tabList = newTabList
                        _tabListFlow.value = newTabList
                        _galleryPageState.value = PageState.PageReady
                    } else {
                        _galleryPageState.value = PageState.PageError
                    }
                }
            }
        } else {
            _tabListFlow.value = tabList
        }
    }

    fun getImages(kinopoiskId: Int, imagesType: String): Flow<PagingData<Images>> {
        val pagedImages: Flow<PagingData<Images>> = Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = {
                ImagesCollectionPagingSource(
                    kinopoiskId,
                    imagesType,
                    galleryImagesRepository
                )
            }
        ).flow.cachedIn(viewModelScope)
        return pagedImages
    }


    private fun errorObserver() {
        viewModelScope.launch {
            tabListRepository.errorsListFlow.collect { newErrorsList ->
                if (newErrorsList.isNotEmpty()) {
                    _galleryErrorFlow.value = newErrorsList
                }
            }
        }
    }


}