package com.example.skillcinema.profilefragment.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemViewedCollectionMovieCardBinding
import com.example.skillcinema.localdatabase.entities.LocalItemEntity


class ProfileFragmentViewedCollectionAdapter(
    private val onItemClick: (LocalItemEntity) -> Unit
) : ListAdapter<LocalItemEntity, ProfileFragmentViewedCollectionViewHolder>(
    ProfileFragmentViewedDiffUtils()
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileFragmentViewedCollectionViewHolder {
        return ProfileFragmentViewedCollectionViewHolder(
            ItemViewedCollectionMovieCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(
        holder: ProfileFragmentViewedCollectionViewHolder,
        position: Int
    ) {
        val item = getItem(position)

        if(item != null){
            if (item.title != null){
                holder.binding.filmTitle.text = item.title
            }

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


            Glide
                .with(holder.binding.root)
                .load(item.posterUrl)
                .into(holder.binding.filmPoster)

            holder.binding.movieCardLayout.setOnClickListener {
                onItemClick(item)
            }
        }

    }
}


class ProfileFragmentViewedDiffUtils : DiffUtil.ItemCallback<LocalItemEntity>() {
    override fun areItemsTheSame(oldItem: LocalItemEntity, newItem: LocalItemEntity): Boolean =
        oldItem.itemId == newItem.itemId


    override fun areContentsTheSame(
        oldItem: LocalItemEntity,
        newItem: LocalItemEntity
    ): Boolean =
        oldItem == newItem

}

class ProfileFragmentViewedCollectionViewHolder(val binding: ItemViewedCollectionMovieCardBinding) :
    RecyclerView.ViewHolder(binding.root)
