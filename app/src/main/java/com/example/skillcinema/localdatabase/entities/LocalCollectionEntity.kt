package com.example.skillcinema.localdatabase.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "collection_table")
data class LocalCollectionEntity(
    @PrimaryKey
    @ColumnInfo(name = "collection_id")
    val collectionId: Int,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "icon")
    val icon: Int,
    @ColumnInfo(name = "size")
    val size: Int,
)
