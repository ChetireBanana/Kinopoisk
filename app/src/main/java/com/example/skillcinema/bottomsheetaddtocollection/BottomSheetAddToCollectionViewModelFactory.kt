package com.example.skillcinema.bottomsheetaddtocollection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class BottomSheetAddToCollectionViewModelFactory @Inject constructor(
    private val viewModel: BottomSheetAddToCollectionViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BottomSheetAddToCollectionViewModel::class.java)) {
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}