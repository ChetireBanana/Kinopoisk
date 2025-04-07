package com.example.skillcinema.fullscreenpaginggallerydialog.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class FullScreenPagingGalleryViewModelFactory @Inject constructor(
    private val viewModel: FullScreenPagingGalleryViewModel
):ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FullScreenPagingGalleryViewModel::class.java)){
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}