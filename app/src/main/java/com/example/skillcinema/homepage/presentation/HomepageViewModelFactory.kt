package com.example.skillcinema.homepage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class HomepageViewModelFactory @Inject constructor(
    private val viewModel: HomepageViewModel
): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomepageViewModel::class.java)){
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}