package com.example.skillcinema.gallerylistpage.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import javax.inject.Inject

class ImagesCollectionPagingSource @Inject constructor(
    private val kinopoiskId: Int,
    private val imagesType: String,
    private val galleryImagesRepository: GalleryImagesRepository
) : PagingSource<Int, Images>(){

    override val keyReuseSupported: Boolean = true

    override fun getRefreshKey(state: PagingState<Int, Images>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Images> {
        val page = params.key ?: FIRST_PAGE

        return kotlin.runCatching {
            galleryImagesRepository.getFilmImagesCollection(kinopoiskId, imagesType, page)
        }.fold(
            onSuccess = { response ->
                val items = response?.items ?: emptyList()
                LoadResult.Page(
                    data = items,
                    prevKey = if (page > 1) page - 1 else null,
                    nextKey = if (items.isNotEmpty()) page + 1 else null
                )
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