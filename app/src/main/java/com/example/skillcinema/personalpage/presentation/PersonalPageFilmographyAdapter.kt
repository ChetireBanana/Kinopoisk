package com.example.skillcinema.personalpage.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.application.presentation.professionKeyTranslator
import com.example.skillcinema.data.models.PersonInfoFilmDto
import com.example.skillcinema.databinding.ItemMovieCardBinding

class PersonalPageFilmographyAdapter(
    private val personalPageFilmographyAdapterInterface: PersonalPageFilmographyAdapterInterface

) : RecyclerView.Adapter<PersonalPageFilmographyViewHolder>() {

    private var data: MutableList<PersonInfoFilmDto> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<PersonInfoFilmDto>) {
        this.data = data.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PersonalPageFilmographyViewHolder {

        return PersonalPageFilmographyViewHolder(
            ItemMovieCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return if (data.size >= 10) {
            10
        } else {
            data.size
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onBindViewHolder(holder: PersonalPageFilmographyViewHolder, position: Int) {
        val item = data[position]
        holder.binding.filmTitle.text = item.nameRu ?: item.nameEn

        val profession = when (item.professionKey) {
            "ACTOR" -> item.description
            "HIMSELF" -> item.description
            "HERSELF" -> item.description
            else -> professionKeyTranslator(holder.binding.filmTitle.context, item.professionKey)
        }

        holder.binding.movieDescription.text = profession

        if (item.rating != null) {
            holder.binding.filmRating.text = item.rating.toString()
        } else {
            holder.binding.filmRating.visibility = View.GONE
        }

        Glide
            .with(holder.binding.filmPoster)
            .load(item.posterUrlPreview)
            .into(holder.binding.filmPoster)


        holder.binding.movieCardLayout.setOnClickListener {
            personalPageFilmographyAdapterInterface.onItemClick(item)
        }

        personalPageFilmographyAdapterInterface.checkIfViewed(item.filmId) { isViewed ->
            if (isViewed) {
                holder.binding.filmPoster.foreground =
                    holder.binding.root.context.getDrawable(R.drawable.seen_gradient_filler)
                holder.binding.viewedMarker.visibility = View.VISIBLE
            } else {
                holder.binding.filmPoster.foreground = null
                holder.binding.viewedMarker.visibility = View.GONE
            }
        }
    }

    interface PersonalPageFilmographyAdapterInterface {
        fun onItemClick(item: PersonInfoFilmDto)
        fun checkIfViewed(itemId: Int, callback: (Boolean) -> Unit)
    }
}


class PersonalPageFilmographyViewHolder(val binding: ItemMovieCardBinding) :
    RecyclerView.ViewHolder(binding.root)