package com.example.skillcinema.filmlistpage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.skillcinema.filmlistpage.data.FilmCollectionPagingSource
import com.example.skillcinema.data.FilmCollectionsRepository
import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.profilefragment.data.LocalDataBaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FilmCollectionListPageViewModel @Inject constructor(
    private val filmCollectionsRepository: FilmCollectionsRepository,
    private val localDataBaseRepository: LocalDataBaseRepository
): ViewModel(){

    fun getPagedMovieCollection(filmsCollection: FilmCollection): Flow<PagingData<FilmDto>> {
        val pagedMovies: Flow<PagingData<FilmDto>> = Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                FilmCollectionPagingSource(
                    filmsCollection,
                    filmCollectionsRepository
                )
            }
        ).flow.cachedIn(viewModelScope)
        return pagedMovies
    }

    fun checkIfViewed(id: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                localDataBaseRepository.checkIfItemIsInCollection(
                    id,
                    LocalBaseCollections.VIEWED_ID
                )
            }
            callback(result)
        }
    }
}