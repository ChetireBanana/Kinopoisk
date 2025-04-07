package com.example.skillcinema.profilefragment.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemMovieCardBinding
import com.example.skillcinema.localdatabase.entities.LocalItemEntity


class InterestingFilmsAdapter(
    private val profileFragmentCollectionAdapterInterface: ProfileFragmentCollectionAdapterInterface
) : ListAdapter<LocalItemEntity, InterestingFilmsViewHolder>(InterestingFilmsDiffutils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestingFilmsViewHolder {
        return InterestingFilmsViewHolder(
            ItemMovieCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: InterestingFilmsViewHolder, position: Int) {
        val item = getItem(position)

        when (item.type) {
            "FILM" -> {
                holder.binding.filmTitle.text = item.title

                if (item.genre != null) {
                    holder.binding.movieDescription.text = item.genre
                } else {
                    holder.binding.movieDescription.visibility = View.GONE
                }

                if (item.rating != null) {
                    holder.binding.filmRating.text = item.rating.toString()
                } else {
                    holder.binding.filmRating.visibility = View.GONE
                }

                profileFragmentCollectionAdapterInterface.checkIfViewed(item.itemId) { isViewed ->
                    if (isViewed) {
                        holder.binding.viewedMarker.visibility = View.VISIBLE
                        holder.binding.filmPoster.foreground =
                            holder.binding.filmPoster.context.getDrawable(R.drawable.seen_gradient_filler)
                    } else {
                        holder.binding.viewedMarker.visibility = View.INVISIBLE
                        holder.binding.filmPoster.foreground = null
                    }
                }
            }

            "PERSON" -> {
                holder.binding.filmRating.visibility = View.GONE
                holder.binding.filmTitle.text = item.name
                holder.binding.movieDescription.text = item.profession
                holder.binding.viewedMarker.visibility = View.INVISIBLE
                holder.binding.filmPoster.foreground = null
            }
        }

        Glide
            .with(holder.binding.root)
            .load(item.posterUrl)
            .into(holder.binding.filmPoster)

        holder.binding.movieCardLayout.setOnClickListener {
            profileFragmentCollectionAdapterInterface.onItemClick(item)
        }
    }

    interface ProfileFragmentCollectionAdapterInterface {
        fun onItemClick(item: LocalItemEntity)
        fun checkIfViewed(id: Int, callback: (Boolean) -> Unit)
    }
}


class InterestingFilmsDiffutils : DiffUtil.ItemCallback<LocalItemEntity>() {
    override fun areItemsTheSame(oldItem: LocalItemEntity, newItem: LocalItemEntity): Boolean =
        oldItem.itemId == newItem.itemId


    override fun areContentsTheSame(oldItem: LocalItemEntity, newItem: LocalItemEntity): Boolean =
        oldItem == newItem

}

class InterestingFilmsViewHolder(val binding: ItemMovieCardBinding) :
    RecyclerView.ViewHolder(binding.root)