package com.example.skillcinema.localdatabase.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class LocalCollectionWithFilms(
    @Embedded
    val localCollectionEntity: LocalCollectionEntity,
    @Relation(
        parentColumn = "collectionId",
        entityColumn = "filmId",
        associateBy = Junction(
            CollectionItemEntity::class,
            parentColumn = "collection_id",
            entityColumn = "film_id"
        )
    )
    val films: List<LocalItemEntity>
    )
