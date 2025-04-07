package com.example.skillcinema.gallerylistpage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class GalleryViewModelFactory @Inject constructor(
    private val viewModel: GalleryViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}