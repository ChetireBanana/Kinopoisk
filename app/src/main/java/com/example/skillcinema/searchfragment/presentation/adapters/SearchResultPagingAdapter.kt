package com.example.skillcinema.searchfragment.presentation.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.databinding.ItemFilmographyFilmCardBinding

class SearchResultPagingAdapter(
    private val searchResultPagingAdapterInterface: SearchResultPagingAdapterInterface
) : PagingDataAdapter<FilmDto, SearchResultPagingAdapterViewHolder>(
    SearchResultPagingAdapterDiffUtilCallback()
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultPagingAdapterViewHolder {
        return SearchResultPagingAdapterViewHolder(
            ItemFilmographyFilmCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onBindViewHolder(holder: SearchResultPagingAdapterViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.binding.filmographyFilmTitle.text = item.nameRu ?: item.nameOriginal

            val filmYear = item.year
            val filmGenres = if(item.genres.isNotEmpty()){item.genres[0].genre} else null

            val filmDescription =
                mutableListOf(filmYear, filmGenres).filterNotNull().joinToString(", ")

            holder.binding.filmographyFilmDescription.text = filmDescription

            if (item.ratingKinopoisk == null) {
                if (item.ratingImdb != null) {
                    holder.binding.filmRating.text = item.ratingImdb.toString()
                } else {
                    holder.binding.filmRating.visibility = View.INVISIBLE
                }
            } else {
                holder.binding.filmRating.text = item.ratingKinopoisk.toString()
            }
            Glide
                .with(holder.binding.filmPoster)
                .load(item.posterUrlPreview)
                .into(holder.binding.filmPoster)

            searchResultPagingAdapterInterface.checkIfViewed(item.kinopoiskId) { isViewed ->
                Log.d("checkIfViewed", "${item.nameRu?: item.nameOriginal} $isViewed")
                if (isViewed) {
                    holder.binding.filmographyFilmCardViewedMarker.visibility = View.VISIBLE
                    holder.binding.filmPoster.foreground =
                        holder.binding.filmPoster.context.getDrawable(R.drawable.seen_gradient_filler)
                } else {
                    holder.binding.filmographyFilmCardViewedMarker.visibility = View.INVISIBLE
                    holder.binding.filmPoster.foreground = null
                }
            }


            holder.binding.filmographyFilmCardLayout.setOnClickListener {
                searchResultPagingAdapterInterface.onItemClick(item)
            }
        }else {
            Log.e("SearchResultPagingAdapter", "Item at position $position is null")
        }
    }

    interface SearchResultPagingAdapterInterface{
        fun onItemClick(filmDto: FilmDto)
        fun checkIfViewed(id: Int, callback: (Boolean) -> Unit)
    }

}


class SearchResultPagingAdapterDiffUtilCallback : DiffUtil.ItemCallback<FilmDto>() {
    override fun areItemsTheSame(oldItem: FilmDto, newItem: FilmDto): Boolean =
        oldItem.kinopoiskId == newItem.kinopoiskId

    override fun areContentsTheSame(oldItem: FilmDto, newItem: FilmDto): Boolean =
        oldItem == newItem


}

class SearchResultPagingAdapterViewHolder(val binding: ItemFilmographyFilmCardBinding) :
    RecyclerView.ViewHolder(binding.root)