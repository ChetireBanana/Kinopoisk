package com.example.skillcinema.bottomsheetaddtocollection

import androidx.lifecycle.ViewModel
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.localdatabase.entities.CollectionItemEntity
import com.example.skillcinema.localdatabase.entities.LocalCollectionEntity
import com.example.skillcinema.localdatabase.entities.LocalItemEntity
import com.example.skillcinema.profilefragment.data.LocalDataBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BottomSheetAddToCollectionViewModel @Inject constructor(
    private val localDataBaseRepository: LocalDataBaseRepository

) : ViewModel() {



    suspend fun filmInfoObserver(itemId: Int): LocalItemEntity {
        return localDataBaseRepository.getItemFromDbById(itemId)
    }


    fun collectionsWithFilmObserver(filmId: Int): Flow<List<CollectionItemEntity>> {
          return localDataBaseRepository.getCollectionsFlowForItemId(filmId)
    }

    suspend fun checkCollectionWithFilm(collectionId: Int, filmId: Int, isChecked: Boolean) {
            if (isChecked){
                localDataBaseRepository.insertItemInLocalCollectionAndResetSize(collectionId, filmId)
            } else {
                localDataBaseRepository.deleteItemFromLocalCollectionAndResetSize(collectionId, filmId)
            }
    }


    fun allCollectionsObserver(): Flow<List<LocalCollectionEntity>> {
        return localDataBaseRepository.getAllCollectionsFromDB().map { collections ->
            collections.filterNot { it.collectionId == LocalBaseCollections.INTERESTING_ID }
        }
    }

}