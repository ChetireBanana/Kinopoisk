package com.example.skillcinema.filmlistpage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class FilmCollectionListPageViewModelFactory @Inject constructor(
    private val viewModel: FilmCollectionListPageViewModel
): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilmCollectionListPageViewModel::class.java)){
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}