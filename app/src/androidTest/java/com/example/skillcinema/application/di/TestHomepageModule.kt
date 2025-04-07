package com.example.skillcinema.application.di


import com.example.skillcinema.FakeHomepageListOfFilmCollectionsUseCase
import com.example.skillcinema.FakeLocalDBRepository
import com.example.skillcinema.homepage.domain.HomepageListOfFilmCollectionsUseCase
import com.example.skillcinema.profilefragment.data.LocalDBRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton


@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppBindsModule::class]
)
object TestHomepageModule {

    @Provides
    @Singleton
    fun provideFakeHomepageUseCase(): HomepageListOfFilmCollectionsUseCase {
        return FakeHomepageListOfFilmCollectionsUseCase()
    }

    @Provides
    @Singleton
    fun provideFakeLocalDatabase(): LocalDBRepository {
        return FakeLocalDBRepository()
    }
}