package com.example.skillcinema.homepage.presentation.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.application.presentation.MAX_ELEMENT_IN_HOMEPAGE_LIST
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.databinding.ItemMovieCardBinding


class MovieCollectionAdapter(
    private val movieCollectionAdapterInterface: MovieCollectionAdapterInterface
) : RecyclerView.Adapter<MovieCardAdapterViewHolder>() {

    private var data: MutableList<FilmDto> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<FilmDto>) {
        this.data = data.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieCardAdapterViewHolder {

        return MovieCardAdapterViewHolder(
            ItemMovieCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return if (data.size >= MAX_ELEMENT_IN_HOMEPAGE_LIST) {
            MAX_ELEMENT_IN_HOMEPAGE_LIST
        } else {
            data.size
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onBindViewHolder(holder: MovieCardAdapterViewHolder, position: Int) {
        val item = data[position]
        holder.binding.filmTitle.text = item.nameRu ?: item.nameOriginal
        if (item.genres.isNotEmpty()) {
            holder.binding.movieDescription.text = item.genres[0].genre
        }
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

        movieCollectionAdapterInterface.checkIfViewed(item.kinopoiskId) { isViewed ->
            Log.d("checkIfViewed", "${item.nameRu ?: item.nameOriginal} $isViewed")
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
            movieCollectionAdapterInterface.onItemClick(item)
        }

    }

    interface MovieCollectionAdapterInterface {
        fun onItemClick(item: FilmDto)
        fun checkIfViewed(id: Int, callback: (Boolean) -> Unit)
    }
}


class MovieCardAdapterViewHolder(val binding: ItemMovieCardBinding) :
    RecyclerView.ViewHolder(binding.root)


