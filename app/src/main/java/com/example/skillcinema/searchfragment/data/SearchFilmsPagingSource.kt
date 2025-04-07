package com.example.skillcinema.searchfragment.data


import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.profilefragment.data.LocalDataBaseRepository
import javax.inject.Inject

class SearchFilmsPagingSource @Inject constructor(
    private val filters: Map<String, String?>,
    private val filmRepository: SearchResultsRepository,
    private val filterNoViewed: Boolean,
    private val localDataBaseRepository: LocalDataBaseRepository

) : PagingSource<Int, FilmDto>() {
    override fun getRefreshKey(state: PagingState<Int, FilmDto>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmDto> {

        val page = params.key ?: FIRST_PAGE
        Log.d("SearchFragment", "Load page $page")

        return kotlin.runCatching {
            val result = filmRepository.getSearchResults(
                filters = filters,
                page = page,
            )
            val totalPages = result?.totalPages ?: 0
            val filteredItems =
                if (filterNoViewed) {
                    result?.items?.filter { filmDto ->
                        !localDataBaseRepository.checkIfItemIsInCollection(
                            filmDto.kinopoiskId,
                            LocalBaseCollections.VIEWED_ID
                        )
                    } ?: emptyList()
                } else {
                    result?.items?: emptyList()
                }

            LoadResult.Page(
                data = filteredItems,
                prevKey = if (page > FIRST_PAGE) page - 1 else null,
                nextKey = if (page < totalPages) page + 1 else null
            )
        }.fold(
            onSuccess = {
                it
            },
            onFailure = {
                LoadResult.Error(it)
            }
        )
    }


    private companion object {
        private const val FIRST_PAGE = 1
    }
}