package com.example.skillcinema.localdatabase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.skillcinema.localdatabase.data.LocalItemType
import com.example.skillcinema.localdatabase.entities.LocalCollectionEntity
import com.example.skillcinema.localdatabase.entities.CollectionItemEntity
import com.example.skillcinema.localdatabase.entities.LocalItemEntity
import com.example.skillcinema.localdatabase.entities.NewLocalCollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommonDao {

    @Query("SELECT * FROM item_table")
    fun getAllItemsFromDB(): Flow<List<LocalItemEntity>>

    @Query("SELECT * FROM item_table WHERE type = '${LocalItemType.FILM}'")
    fun getAllFilmsFromDB(): Flow<List<LocalItemEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItemToDB(localItemEntity: LocalItemEntity)

    @Delete
    suspend fun deleteItemFromDB(localItemEntity: LocalItemEntity)

    @Query("SELECT * FROM item_table WHERE item_id = :id" )
    suspend fun getItemById(id: Int):LocalItemEntity

    @Update
    suspend fun updateItem(localItemEntity: LocalItemEntity)

    @Query("DELETE FROM item_table")
    suspend fun deleteAllItemsFromDB()

    @Query("SELECT * FROM collection_table")
    fun getAllCollectionsFromDB(): Flow<List<LocalCollectionEntity>>

//    @Query("SELECT * FROM collection_table WHERE title = :title")
//    fun getLocalCollectionFlowByTitle(title: String): Flow<LocalCollectionEntity>

    @Delete
    suspend fun deleteCollectionFromDB(localCollectionEntity: LocalCollectionEntity)

    @Query("DELETE FROM collection_table WHERE collection_id = :localCollectionId")
    suspend fun deleteCollectionFromDBById(localCollectionId: Int)

    @Insert(entity = LocalCollectionEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewCollectionInDB(collectionEntity: NewLocalCollectionEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM collection_table WHERE LOWER(title)  = LOWER(:title))")
    suspend fun checkIfLocalCollectionExistByTitle(title: String): Boolean

    @Transaction
    suspend fun clearCollectionDeleteItemIfNoDuplicatesAndResetSize(collectionId: Int) {
        val items = getAllItemListForCollection(collectionId)
        items.forEach { item ->
            val duplicates = getCollectionsForItemId(item.itemId).filterNot { it.collectionId == collectionId }
            if(duplicates.isEmpty()){
                val localItemEntity = getItemById(item.itemId)
                deleteItemFromDB(localItemEntity)
            }
        }
        clearCollection(collectionId)
        updateCollectionSize(collectionId, 0)
    }

    @Transaction
    suspend fun insertItemInLocalCollectionAndResetSize(collectionItemEntity: CollectionItemEntity){
        insertCollectionItemToDb(collectionItemEntity)
        val collectionId = collectionItemEntity.collectionId
        val newSize = getAllItemListForCollection(collectionId).size
        updateCollectionSize(collectionId, newSize)
    }

    @Transaction
    suspend fun deleteItemFromLocalCollectionAndResetSize(collectionId: Int, itemId: Int) {
        deleteItemFromCollection(collectionId, itemId)
        val newSize = getAllItemListForCollection(collectionId).size
        updateCollectionSize(collectionId, newSize)
        val duplicates = getCollectionsForItemId(itemId)
        if(duplicates.isEmpty()){
            val localItemEntity = getItemById(itemId)
            deleteItemFromDB(localItemEntity)
        }
    }

    @Query("DELETE FROM collection_item_entity WHERE collection_id = :collectionId")
    suspend fun clearCollection(collectionId: Int)

    @Query("SELECT * FROM collection_table WHERE collection_id = :collectionId" )
    fun getCollectionById(collectionId: Int):Flow<LocalCollectionEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCollectionItemToDb(collectionItemEntity: CollectionItemEntity)

    @Query("DELETE FROM collection_item_entity WHERE collection_id = :collectionId and item_id = :itemId")
    suspend fun deleteItemFromCollection(collectionId: Int, itemId: Int)

    @Query ("UPDATE collection_table SET size = :newSize WHERE collection_id = :collectionId")
    suspend fun updateCollectionSize(collectionId: Int, newSize: Int)

    @Query("SELECT COUNT(*) FROM collection_item_entity WHERE collection_id = :collectionId")
    suspend fun getCollectionSize(collectionId: Int): Int

    @Query("SELECT * FROM collection_item_entity WHERE item_id = :itemId")
    suspend fun getCollectionsForItemId(itemId: Int): List<CollectionItemEntity>

    @Query("SELECT * FROM collection_item_entity WHERE item_id = :itemId")
    fun getCollectionsFlowForItemId(itemId: Int): Flow<List<CollectionItemEntity>>

    @Query("SELECT *  FROM collection_item_entity WHERE collection_id = :collectionId")
    suspend fun getAllItemListForCollection(collectionId: Int): List<CollectionItemEntity>

    @Query("SELECT * FROM collection_item_entity WHERE collection_id = :collectionId")
    fun getAllItemsFlowForCollection(collectionId: Int): Flow<List<CollectionItemEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM collection_item_entity WHERE collection_id = :collectionId and item_id = :itemId)")
    suspend fun checkIfItemIsInCollection(itemId: Int, collectionId: Int): Boolean

}