package com.example.skillcinema.localcollectionslistpage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.localdatabase.entities.LocalCollectionEntity
import com.example.skillcinema.localdatabase.entities.LocalItemEntity
import com.example.skillcinema.profilefragment.data.LocalDataBaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalCollectionListPageViewModel @Inject constructor(
    private val localDataBaseRepository: LocalDataBaseRepository
) : ViewModel() {

    fun getCollectionById(collectionId: Int): Flow<LocalCollectionEntity> {
          return localDataBaseRepository.getCollectionFromDbById(collectionId)
    }

    fun getAllItemsFlowForCollection(collectionId: Int): Flow<List<LocalItemEntity>> {
        return localDataBaseRepository.getAllItemsFlowForCollection(collectionId)
    }


    fun clearCollectionById(collectionId: Int) {
        viewModelScope.launch {
            localDataBaseRepository.clearCollection(collectionId)
        }
    }

    fun checkIfViewed(id: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                localDataBaseRepository.checkIfItemIsInCollection(
                    id,
                    LocalBaseCollections.VIEWED_ID
                )
            }
            callback(result)
        }
    }
}