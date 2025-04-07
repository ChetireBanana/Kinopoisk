package com.example.skillcinema.profilefragment.data

import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.localdatabase.dao.CommonDao
import com.example.skillcinema.localdatabase.data.FilmLocalItem
import com.example.skillcinema.localdatabase.data.PersonLocalItem
import com.example.skillcinema.localdatabase.entities.CollectionItemEntity
import com.example.skillcinema.localdatabase.entities.LocalCollectionEntity
import com.example.skillcinema.localdatabase.entities.LocalItemEntity
import com.example.skillcinema.localdatabase.entities.NewLocalCollectionEntity
import com.example.skillcinema.personalpage.data.PersonInfoDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface LocalDBRepository {
    suspend fun getItemFromDbById(itemId: Int): LocalItemEntity
    fun getCollectionFromDbById(collectionId: Int): Flow<LocalCollectionEntity>
    fun getAllCollectionsFromDB(): Flow<List<LocalCollectionEntity>>
    fun getAllItemsFlowForCollection(collectionId: Int): Flow<List<LocalItemEntity>>
    suspend fun clearCollection(collectionId: Int)
    suspend fun deleteCollectionFromDBById(collectionId: Int)
    suspend fun adItemToDBFromFilm(filmDto: FilmDto)
    suspend fun adItemToDBFromPerson(personInfoDto: PersonInfoDto)
    suspend fun insertItemInLocalCollectionAndResetSize(collectionId: Int, itemId: Int)
    suspend fun checkIfItemIsInCollection(id: Int, collectionId: Int): Boolean
    suspend fun checkIfLocalCollectionExistByTitle(title: String): Boolean
    suspend fun insertCollectionToDB(collection: NewLocalCollectionEntity)
    fun getCollectionsFlowForItemId(itemId: Int): Flow<List<CollectionItemEntity>>
    suspend fun deleteItemFromLocalCollectionAndResetSize(collectionId: Int, itemId: Int)
}

class LocalDataBaseRepository @Inject constructor(
    private val commonDao: CommonDao
): LocalDBRepository {

    override suspend fun getItemFromDbById(itemId: Int): LocalItemEntity {
        return commonDao.getItemById(itemId)
    }

    override fun getCollectionFromDbById(collectionId: Int): Flow<LocalCollectionEntity> {
        return commonDao.getCollectionById(collectionId)
    }

    override fun getAllCollectionsFromDB(): Flow<List<LocalCollectionEntity>> {
        return commonDao.getAllCollectionsFromDB()
    }

    override fun getAllItemsFlowForCollection(collectionId: Int): Flow<List<LocalItemEntity>> {
        val itemsFlow = commonDao.getAllItemsFlowForCollection(collectionId)

        return itemsFlow.map { collectionItems ->
            val localItemEntities = mutableListOf<LocalItemEntity>()
            for (item in collectionItems) {

                val localItem = commonDao.getItemById(item.itemId)

                localItemEntities.add(localItem)
            }
            localItemEntities
        }
    }


    override suspend fun clearCollection(collectionId: Int) {
        commonDao.clearCollectionDeleteItemIfNoDuplicatesAndResetSize(collectionId)
    }

    override suspend fun deleteCollectionFromDBById(collectionId: Int){
        commonDao.deleteCollectionFromDBById(collectionId)
    }

    override suspend fun adItemToDBFromFilm(filmDto: FilmDto){
        commonDao.insertItemToDB(
            LocalItemEntity.fromFilm(
                FilmLocalItem(
                    id = filmDto.kinopoiskId,
                    title = filmDto.nameRu ?: filmDto.nameOriginal ?: "Без названия",
                    rating = filmDto.ratingKinopoisk ?: filmDto.ratingImdb,
                    genre = filmDto.genres[0].genre,
                    posterUrl = filmDto.posterUrl,
                    year = filmDto.year,
                )
            )
        )

    }

    override suspend fun adItemToDBFromPerson(personInfoDto: PersonInfoDto){
        commonDao.insertItemToDB(
            LocalItemEntity.fromPerson(
                PersonLocalItem(
                    id = personInfoDto.personId,
                    name = personInfoDto.nameRu ?: personInfoDto.nameEn ?: "Без имени",
                    profession = personInfoDto.profession ?: "Без профессии",
                    posterUrl = personInfoDto.posterUrl ?: "Без фото",
                )
                )
            )
    }

    override suspend fun insertItemInLocalCollectionAndResetSize(collectionId: Int, itemId: Int) {
        commonDao.insertItemInLocalCollectionAndResetSize(
           CollectionItemEntity(collectionId, itemId)
        )
    }

    override suspend fun checkIfItemIsInCollection(id: Int, collectionId: Int): Boolean {
        return commonDao.checkIfItemIsInCollection(id, collectionId)
    }

    override suspend fun checkIfLocalCollectionExistByTitle(title: String): Boolean{
        return commonDao.checkIfLocalCollectionExistByTitle(title)
    }

    override suspend fun insertCollectionToDB(collection: NewLocalCollectionEntity) {
        commonDao.insertNewCollectionInDB(collection)
    }

    override fun getCollectionsFlowForItemId(itemId: Int): Flow<List<CollectionItemEntity>> {
        return commonDao.getCollectionsFlowForItemId(itemId)
    }

    override suspend fun deleteItemFromLocalCollectionAndResetSize(collectionId: Int, itemId: Int) {
        commonDao.deleteItemFromLocalCollectionAndResetSize(collectionId,  itemId)
    }

}
