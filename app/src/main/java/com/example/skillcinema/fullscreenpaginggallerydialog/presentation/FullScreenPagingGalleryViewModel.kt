package com.example.skillcinema.fullscreenpaginggallerydialog.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.skillcinema.gallerylistpage.data.GalleryImagesRepository
import com.example.skillcinema.gallerylistpage.data.Images
import com.example.skillcinema.gallerylistpage.data.ImagesCollectionPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FullScreenPagingGalleryViewModel @Inject constructor(
    private val galleryImagesRepository: GalleryImagesRepository,
): ViewModel(){


    fun getImages(kinopoiskId: Int, imagesType: String): Flow<PagingData<Images>> {
        Log.d("FullScreenPagingGalleryViewModelGetImages","init $kinopoiskId")
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
        Log.d("FullScreenPagingGalleryViewModelGetImages","pagedImages $pagedImages")
        return pagedImages
    }


}