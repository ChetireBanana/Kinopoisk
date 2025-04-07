package com.example.skillcinema.filmlistpage.presentation

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
import com.example.skillcinema.databinding.ItemMovieCardBinding

class ListPagePagingAdapter(
    private val listPagePagingAdapterInterface: ListPagePagingAdapterInterface
) : PagingDataAdapter<FilmDto, ListPagePagingAdapterViewHolder>(
    ListPagePagingAdapterDiffUtilCallback()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListPagePagingAdapterViewHolder {
        return ListPagePagingAdapterViewHolder(
            ItemMovieCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onBindViewHolder(holder: ListPagePagingAdapterViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.binding.filmTitle.text = item.nameRu ?: item.nameOriginal

            holder.binding.movieDescription.text = item.genres[0].genre
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

            listPagePagingAdapterInterface.checkIfViewed(item.kinopoiskId) { isViewed ->
                Log.d("checkIfViewed", "${item.nameRu?: item.nameOriginal} $isViewed")
                if (isViewed) {
                    holder.binding.viewedMarker.visibility = View.VISIBLE
                    holder.binding.filmPoster.foreground =
                        holder.binding.filmPoster.context.getDrawable(R.drawable.seen_gradient_filler)
                } else {
                    holder.binding.viewedMarker.visibility = View.INVISIBLE
                    holder.binding.filmPoster.foreground = null
                }
            }


            holder.binding.movieCardLayout.setOnClickListener {
                listPagePagingAdapterInterface.onItemClick(item)
            }
        }
    }

    interface ListPagePagingAdapterInterface{
        fun onItemClick(filmDto: FilmDto)
        fun checkIfViewed(id: Int, callback: (Boolean) -> Unit)
    }


}

class ListPagePagingAdapterDiffUtilCallback : DiffUtil.ItemCallback<FilmDto>() {
    override fun areItemsTheSame(oldItem: FilmDto, newItem: FilmDto): Boolean =
        oldItem.kinopoiskId == newItem.kinopoiskId

    override fun areContentsTheSame(oldItem: FilmDto, newItem: FilmDto): Boolean =
        oldItem == newItem


}

class ListPagePagingAdapterViewHolder(val binding: ItemMovieCardBinding) :
    RecyclerView.ViewHolder(binding.root)