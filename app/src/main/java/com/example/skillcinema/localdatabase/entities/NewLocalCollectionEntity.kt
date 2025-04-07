package com.example.skillcinema.localdatabase.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class NewLocalCollectionEntity(
    @PrimaryKey
    @ColumnInfo(name = "collection_id")
    val collectionId: Int? = null,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "icon")
    val icon: Int,
    @ColumnInfo(name = "size")
    val size: Int,
)
