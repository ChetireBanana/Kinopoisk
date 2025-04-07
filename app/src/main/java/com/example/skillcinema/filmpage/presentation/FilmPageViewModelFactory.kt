package com.example.skillcinema.filmpage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class FilmPageViewModelFactory @Inject constructor(
    private val viewModel: FilmPageViewModel
): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilmPageViewModel::class.java)){
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}