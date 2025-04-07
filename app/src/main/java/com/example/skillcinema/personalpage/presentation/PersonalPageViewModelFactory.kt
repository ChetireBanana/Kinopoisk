package com.example.skillcinema.personalpage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class PersonalPageViewModelFactory @Inject constructor(
    private val viewModel: PersonalPageViewModel
): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonalPageViewModel::class.java)) {
            return viewModel as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}