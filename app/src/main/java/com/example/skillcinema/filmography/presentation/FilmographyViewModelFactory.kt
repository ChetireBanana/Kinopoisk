package com.example.skillcinema.filmography.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class FilmographyViewModelFactory @Inject constructor(
    private val viewModel: FilmographyViewModel
): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FilmographyViewModel::class.java)){
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}