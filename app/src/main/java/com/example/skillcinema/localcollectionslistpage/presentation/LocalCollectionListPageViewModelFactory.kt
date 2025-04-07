package com.example.skillcinema.localcollectionslistpage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class LocalCollectionListPageViewModelFactory @Inject constructor(
    private val viewModel: LocalCollectionListPageViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocalCollectionListPageViewModel::class.java)) {
            return viewModel as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")

        }
    }
}