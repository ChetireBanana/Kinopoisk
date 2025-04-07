package com.example.skillcinema.profilefragment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.localdatabase.entities.LocalCollectionEntity
import com.example.skillcinema.localdatabase.entities.LocalItemEntity
import com.example.skillcinema.profilefragment.data.LocalDataBaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val localDataBaseRepository: LocalDataBaseRepository,
) : ViewModel() {

    init {
        getInterestingItemsCollection()
        getViewedCollection()
        getOtherLocalCollectionsFromDB()
    }

    fun getInterestingItemsCollection(): Flow<List<LocalItemEntity>> {
        val collectionId = LocalBaseCollections.INTERESTING_ID
        return localDataBaseRepository.getAllItemsFlowForCollection(collectionId)
    }

    fun getViewedCollection(): Flow<List<LocalItemEntity>> {
        val collectionId = LocalBaseCollections.VIEWED_ID
        return localDataBaseRepository.getAllItemsFlowForCollection(collectionId)
    }

    fun getOtherLocalCollectionsFromDB(): Flow<List<LocalCollectionEntity>> {
        return localDataBaseRepository.getAllCollectionsFromDB().map { collections ->
            collections.filterNot {
                it.collectionId in listOf(
                    LocalBaseCollections.INTERESTING_ID,
                    LocalBaseCollections.VIEWED_ID
                )
            }
        }
    }


    fun deleteCollectionFromDB(collectionId: Int) {
        viewModelScope.launch {
            localDataBaseRepository.deleteCollectionFromDBById(collectionId)
        }
    }

    fun clearCollection(collectionId: Int) {
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
