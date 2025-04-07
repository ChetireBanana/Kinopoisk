package com.example.skillcinema.filmlistpage.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.skillcinema.data.FilmCollectionsRepository
import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.data.models.extras.FilmCollectionTags
import javax.inject.Inject


class FilmCollectionPagingSource @Inject constructor(
    private val filmCollection: FilmCollection,
    private val filmRepository: FilmCollectionsRepository,

    ) : PagingSource<Int, FilmDto>() {
    override fun getRefreshKey(state: PagingState<Int, FilmDto>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmDto> {

        val title = filmCollection.title
        val filters = filmCollection.filters
        val page = params.key ?: FIRST_PAGE


        when (filmCollection.tag) {
            FilmCollectionTags.TOP_LIST_PAGED_COLLECTION -> {
                return kotlin.runCatching {
                    filmRepository.getTopListFilmCollection(
                        title!!,
                        filters!!,
                        page
                    )
                }.fold(
                    onSuccess = {
                        LoadResult.Page(
                            data = it?.items ?: emptyList(),
                            prevKey = null,
                            nextKey = if (it?.items?.isEmpty() == true) null else page + 1

                        )
                    },
                    onFailure = { error ->
                        LoadResult.Error(error)
                    }
                )
            }

            FilmCollectionTags.FILM_AND_SERIALS_BY_FILTERS_PAGED_COLLECTION -> {
                return kotlin.runCatching {
                    filmRepository.getFilmsAndSerialsByFilters(
                        title = title!!,
                        filters!!,
                        page
                    )
                }.fold(
                    onSuccess = {
                        LoadResult.Page(
                            data = it?.items ?: emptyList(),
                            prevKey = null,
                            nextKey = if (it?.items?.isEmpty() == true) null else page + 1
                        )
                    },
                    onFailure = { error ->
                        LoadResult.Error(error)
                    }
                )
            }

            else -> throw IllegalStateException("Unknown tag")
        }
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}