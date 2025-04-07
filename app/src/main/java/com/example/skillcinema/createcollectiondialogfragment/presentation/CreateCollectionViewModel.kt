package com.example.skillcinema.createcollectiondialogfragment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.localdatabase.entities.NewLocalCollectionEntity
import com.example.skillcinema.profilefragment.data.LocalDataBaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateCollectionViewModel @Inject constructor(
    private val localDataBaseRepository: LocalDataBaseRepository
): ViewModel() {

    fun insertCollectionToDB(collection: NewLocalCollectionEntity) {
        viewModelScope.launch {
            localDataBaseRepository.insertCollectionToDB(collection)
        }
    }

    fun checkIfLocalCollectionExistByTitle(title: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                localDataBaseRepository.checkIfLocalCollectionExistByTitle(title)
            }
            callback(result)
        }
    }
}