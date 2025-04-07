package com.example.skillcinema.gallerylistpage.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.skillcinema.gallerylistpage.data.Images
import com.example.skillcinema.databinding.ItemGalleryBinding

class GalleryImagesPagingAdapter(
    private val onItemClick: (Int) -> Unit
):PagingDataAdapter<Images, GalleryImagesViewHolder>(
    GalleryImagesPagingAdapterDiffCallback()
){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryImagesViewHolder {
        return GalleryImagesViewHolder(
            ItemGalleryBinding.inflate(
                LayoutInflater.from(parent.context),parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: GalleryImagesViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            Glide
                .with(holder.binding.itemGalleryPoster)
                .load(item.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.itemGalleryPoster)

            holder.binding.itemGalleryPoster.setOnClickListener{
                onItemClick(position)
            }
        }
    }
}

class GalleryImagesPagingAdapterDiffCallback : DiffUtil.ItemCallback<Images>() {
    override fun areItemsTheSame(oldItem: Images, newItem: Images): Boolean =
        oldItem.imageUrl == newItem.imageUrl


    override fun areContentsTheSame(oldItem: Images, newItem: Images): Boolean =
        oldItem == newItem
}

class GalleryImagesViewHolder (val binding: ItemGalleryBinding):RecyclerView.ViewHolder(binding.root)
