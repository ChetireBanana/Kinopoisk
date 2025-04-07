package com.example.skillcinema.localdatabase.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.skillcinema.R
import com.example.skillcinema.localdatabase.dao.CommonDao
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.localdatabase.entities.LocalCollectionEntity
import com.example.skillcinema.localdatabase.entities.CollectionItemEntity
import com.example.skillcinema.localdatabase.entities.LocalItemEntity
import com.example.skillcinema.localdatabase.entities.NewLocalCollectionEntity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        LocalItemEntity::class,
        LocalCollectionEntity::class,
        CollectionItemEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun commonDao(): CommonDao

    companion object {
        private const val DATABASE_NAME = "itemDb"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun createInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    initializeDefaultCollections(instance)

                    INSTANCE = instance
                }
                return instance
            }
        }

        @OptIn(DelicateCoroutinesApi::class)
        private fun initializeDefaultCollections(database: AppDatabase) {
            GlobalScope.launch(Dispatchers.IO) {
                val collectionDao = database.commonDao()
                collectionDao.insertNewCollectionInDB(
                    NewLocalCollectionEntity(
                        collectionId = LocalBaseCollections.INTERESTING_ID,
                        title = LocalBaseCollections.INTERESTING_TITLE,
                        icon = R.drawable.icon_profile,
                        size = 0
                    )
                )
                collectionDao.insertNewCollectionInDB(
                    NewLocalCollectionEntity(
                        collectionId = LocalBaseCollections.FAVOURITE_ID,
                        title = LocalBaseCollections.FAVOURITE_TITLE,
                        icon = R.drawable.icon_heart,
                        size = 0
                    )
                )
                collectionDao.insertNewCollectionInDB(
                    NewLocalCollectionEntity(
                        collectionId = LocalBaseCollections.BOOKMARK_ID,
                        title = LocalBaseCollections.BOOKMARK_TITLE,
                        icon = R.drawable.icon_bookmark,
                        size = 0
                    )
                )
                collectionDao.insertNewCollectionInDB(
                    NewLocalCollectionEntity(
                        collectionId = LocalBaseCollections.VIEWED_ID,
                        title = LocalBaseCollections.VIEWED_TITLE,
                        icon = R.drawable.icon_eye,
                        size = 0
                    )
                )
            }


        }
    }
}