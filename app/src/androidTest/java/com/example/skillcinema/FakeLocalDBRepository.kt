package com.example.skillcinema

import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.localdatabase.entities.CollectionItemEntity
import com.example.skillcinema.localdatabase.entities.LocalCollectionEntity
import com.example.skillcinema.localdatabase.entities.LocalItemEntity
import com.example.skillcinema.localdatabase.entities.NewLocalCollectionEntity
import com.example.skillcinema.personalpage.data.PersonInfoDto
import com.example.skillcinema.profilefragment.data.LocalDBRepository
import kotlinx.coroutines.flow.Flow

class FakeLocalDBRepository(): LocalDBRepository {

    init {
        println("FakeLocalDBRepository успешно создан")
    }

    override suspend fun getItemFromDbById(itemId: Int): LocalItemEntity {
        TODO("Not yet implemented")
    }

    override fun getCollectionFromDbById(collectionId: Int): Flow<LocalCollectionEntity> {
        TODO("Not yet implemented")
    }

    override fun getAllCollectionsFromDB(): Flow<List<LocalCollectionEntity>> {
        TODO("Not yet implemented")
    }

    override fun getAllItemsFlowForCollection(collectionId: Int): Flow<List<LocalItemEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun clearCollection(collectionId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCollectionFromDBById(collectionId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun adItemToDBFromFilm(filmDto: FilmDto) {
        TODO("Not yet implemented")
    }

    override suspend fun adItemToDBFromPerson(personInfoDto: PersonInfoDto) {
        TODO("Not yet implemented")
    }

    override suspend fun insertItemInLocalCollectionAndResetSize(collectionId: Int, itemId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun checkIfItemIsInCollection(id: Int, collectionId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun checkIfLocalCollectionExistByTitle(title: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun insertCollectionToDB(collection: NewLocalCollectionEntity) {
        TODO("Not yet implemented")
    }

    override fun getCollectionsFlowForItemId(itemId: Int): Flow<List<CollectionItemEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItemFromLocalCollectionAndResetSize(collectionId: Int, itemId: Int) {
        TODO("Not yet implemented")
    }
}