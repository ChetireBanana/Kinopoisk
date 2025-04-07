package com.example.skillcinema.application.di

import android.content.Context
import com.example.skillcinema.localdatabase.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext appContext: Context
    ): AppDatabase {
        return AppDatabase.createInstance(appContext)
    }


    @Singleton
    @Provides
    fun provideCommonDao(db: AppDatabase) = db.commonDao()

}