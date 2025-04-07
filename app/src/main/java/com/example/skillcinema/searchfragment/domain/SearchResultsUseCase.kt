package com.example.skillcinema.searchfragment.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.profilefragment.data.LocalDataBaseRepository
import com.example.skillcinema.searchfragment.data.SearchFilmsPagingSource
import com.example.skillcinema.searchfragment.data.SearchResultsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchResultsUseCase @Inject constructor(
    private val searchResultsRepository: SearchResultsRepository,
    private val localDataBaseRepository: LocalDataBaseRepository
) {

    fun getSearchResultsPagedCollection(filters: Map<String, String?>, filterNoViewed: Boolean): Flow<PagingData<FilmDto>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                SearchFilmsPagingSource(
                    filters = filters,
                    searchResultsRepository,
                    filterNoViewed = filterNoViewed,
                    localDataBaseRepository
                )
            }
        ).flow
    }
}
