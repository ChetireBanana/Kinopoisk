package com.example.skillcinema.filmography.presentation.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.application.presentation.professionKeyTranslator
import com.example.skillcinema.data.models.PersonInfoFilmDto
import com.example.skillcinema.databinding.ItemFilmographyFilmCardBinding

class FilmographyPersonFilmListAdapter(
    private val filmographyPersonFilmListInterface: FilmographyPersonFilmListInterface
) : ListAdapter<PersonInfoFilmDto, PersonalPageFilmographyViewHolder>(FilmographyDiffutilsCallback()) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PersonalPageFilmographyViewHolder {
        Log.d("FilmographyPersonFilmListAdapter", "onCreateViewHolder")

        return PersonalPageFilmographyViewHolder(
            ItemFilmographyFilmCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onBindViewHolder(holder: PersonalPageFilmographyViewHolder, position: Int) {
        val item = getItem(position)

        Log.d("FilmographyPersonFilmListAdapter", item.nameRu.toString())
        holder.binding.filmographyFilmTitle.text = item.nameRu ?: item.nameEn

        if (item.genre == null && item.year == null) {
            val profession = when (item.professionKey) {
                "ACTOR" -> item.description
                "HIMSELF" -> item.description
                "HERSELF" -> item.description
                else -> professionKeyTranslator(
                    holder.binding.filmographyFilmTitle.context,
                    item.professionKey
                )
            }

            holder.binding.filmographyFilmDescription.text = profession
        } else {
            val year = item.year
            val genre = item.genre
            holder.binding.filmographyFilmDescription.text = listOfNotNull(year, genre)
                .joinToString(", ")
        }

        if (item.rating != null) {
            holder.binding.filmRating.text = item.rating.toString()
        } else {
            holder.binding.filmRating.visibility = View.GONE
        }

        val image = if (item.posterUrlPreview != null) {
            item.posterUrlPreview
        } else {
            R.raw.noposter
        }

        Glide
            .with(holder.binding.filmPoster)
            .load(image)
            .into(holder.binding.filmPoster)



        holder.binding.root.setOnClickListener {
            filmographyPersonFilmListInterface.onItemClick(item)
        }

        filmographyPersonFilmListInterface.checkIfViewed(item.filmId) { isViewed ->
            if (isViewed) {
                holder.binding.filmographyFilmCardViewedMarker.visibility = RecyclerView.VISIBLE
                holder.binding.filmPoster.foreground =
                    holder.binding.filmPoster.context.getDrawable(R.drawable.seen_gradient_filler)
            } else {
                holder.binding.filmographyFilmCardViewedMarker.visibility = RecyclerView.GONE
                holder.binding.filmPoster.foreground = null
            }
        }
    }

    interface FilmographyPersonFilmListInterface {
        fun onItemClick(item: PersonInfoFilmDto)
        fun checkIfViewed(itemId: Int, callback: (Boolean) -> Unit)
    }
}

class FilmographyDiffutilsCallback : DiffUtil.ItemCallback<PersonInfoFilmDto>() {
    override fun areItemsTheSame(oldItem: PersonInfoFilmDto, newItem: PersonInfoFilmDto): Boolean =
        oldItem.filmId == newItem.filmId


    override fun areContentsTheSame(
        oldItem: PersonInfoFilmDto,
        newItem: PersonInfoFilmDto
    ): Boolean = oldItem == newItem
}


class PersonalPageFilmographyViewHolder(val binding: ItemFilmographyFilmCardBinding) :
    RecyclerView.ViewHolder(binding.root)