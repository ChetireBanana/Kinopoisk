package com.example.skillcinema.application.di

import android.content.Context
import com.example.skillcinema.homepage.domain.CreateHomepageListOfFilmCollectionsUseCase
import com.example.skillcinema.homepage.domain.HomepageListOfFilmCollectionsUseCase
import com.example.skillcinema.localdatabase.db.AppDatabase
import com.example.skillcinema.profilefragment.data.LocalDBRepository
import com.example.skillcinema.profilefragment.data.LocalDataBaseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModule {

    @Binds
    abstract fun bindHomepageFilmCollectionsUseCase(
        impl: CreateHomepageListOfFilmCollectionsUseCase
    ): HomepageListOfFilmCollectionsUseCase

    @Binds
    abstract fun bindLocalDatabase(
        impl: LocalDataBaseRepository
    ): LocalDBRepository
}