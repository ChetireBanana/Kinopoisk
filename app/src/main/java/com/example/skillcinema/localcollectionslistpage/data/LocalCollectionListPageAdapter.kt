package com.example.skillcinema.localcollectionslistpage.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemMovieCardBinding
import com.example.skillcinema.localdatabase.data.LocalItemType
import com.example.skillcinema.localdatabase.entities.LocalItemEntity

class LocalCollectionListPageAdapter(
    private val localCollectionListPageAdapterInterface: LocalCollectionListPageAdapterInterface
) : ListAdapter<LocalItemEntity, LocalCollectionListPageViewHolder>(LocalCollectionListPageDiffutils()) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocalCollectionListPageViewHolder {
        return LocalCollectionListPageViewHolder(
            ItemMovieCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: LocalCollectionListPageViewHolder, position: Int) {
        val item = getItem(position)

        if(item != null){
            when (item.type) {
                LocalItemType.FILM -> {
                    holder.binding.filmTitle.text = item.title
                    holder.binding.movieDescription.text = item.genre
                    if (item.rating != null) {
                        holder.binding.filmRating.text = item.rating.toString()
                        holder.binding.filmRating.visibility = RecyclerView.VISIBLE
                    } else {
                        holder.binding.filmRating.visibility = RecyclerView.GONE
                    }
                }

                LocalItemType.PERSON -> {
                    holder.binding.filmTitle.text = item.name
                    holder.binding.movieDescription.text = item.profession
                    holder.binding.filmRating.visibility = RecyclerView.GONE
                }
            }

            Glide
                .with(holder.binding.filmPoster)
                .load(item.posterUrl)
                .into(holder.binding.filmPoster)

            holder.binding.movieCardLayout.setOnClickListener {
                localCollectionListPageAdapterInterface.onItemClick(
                    item.itemId
                )
            }

            localCollectionListPageAdapterInterface.checkIfViewed(item.itemId) { isViewed ->
                if (isViewed) {
                    holder.binding.viewedMarker.visibility = RecyclerView.VISIBLE
                    holder.binding.filmPoster.foreground =
                        holder.binding.filmPoster.context.getDrawable(R.drawable.seen_gradient_filler)
                } else {
                    holder.binding.viewedMarker.visibility = RecyclerView.GONE
                    holder.binding.filmPoster.foreground = null
                }
            }
        }
    }

    interface LocalCollectionListPageAdapterInterface {
        fun onItemClick(itemId: Int)
        fun checkIfViewed(id: Int, callback: (Boolean) -> Unit)
    }

}

class LocalCollectionListPageDiffutils : DiffUtil.ItemCallback<LocalItemEntity>() {
    override fun areItemsTheSame(oldItem: LocalItemEntity, newItem: LocalItemEntity): Boolean =
        oldItem.itemId == newItem.itemId


    override fun areContentsTheSame(oldItem: LocalItemEntity, newItem: LocalItemEntity): Boolean =
        oldItem == newItem

}

class LocalCollectionListPageViewHolder(val binding: ItemMovieCardBinding) :
    RecyclerView.ViewHolder(binding.root)