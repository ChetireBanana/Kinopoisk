package com.example.skillcinema.profilefragment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory @Inject constructor(
    private val viewModel: ProfileViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}