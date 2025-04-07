package com.example.skillcinema.localdatabase.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity (tableName = "collection_item_entity",
    primaryKeys = ["collection_id", "item_id"]
)
data class CollectionItemEntity(
    @ColumnInfo (name = "collection_id")
    val collectionId: Int,
    @ColumnInfo (name = "item_id")
    val itemId: Int,
)
