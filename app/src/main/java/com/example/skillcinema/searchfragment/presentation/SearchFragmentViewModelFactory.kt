package com.example.skillcinema.searchfragment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class SearchFragmentViewModelFactory @Inject constructor(
    private val viewModel: SearchFragmentViewModel
):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchFragmentViewModel::class.java)) {
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}