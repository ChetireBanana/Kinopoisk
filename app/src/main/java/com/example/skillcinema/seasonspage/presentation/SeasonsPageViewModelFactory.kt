package com.example.skillcinema.seasonspage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class SeasonsPageViewModelFactory @Inject constructor(
    private val viewModel: SeasonsPageViewModel
) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SeasonsPageViewModel::class.java)) {
                return viewModel as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }