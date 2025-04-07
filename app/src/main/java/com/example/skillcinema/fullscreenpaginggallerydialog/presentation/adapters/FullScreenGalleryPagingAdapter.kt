package com.example.skillcinema.fullscreenpaginggallerydialog.presentation.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.skillcinema.databinding.ItemFullScreenGalleryBinding
import com.example.skillcinema.gallerylistpage.data.Images

class FullScreenGalleryPagingAdapter :
    PagingDataAdapter<Images, FullScreenGalleryPagingAdapterViewHolder>(
        FullScreenGalleryPagingAdapterDiffutilsCallBack()
    ) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FullScreenGalleryPagingAdapterViewHolder {
        return FullScreenGalleryPagingAdapterViewHolder(
            ItemFullScreenGalleryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FullScreenGalleryPagingAdapterViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            Glide
                .with(holder.binding.fullScreenGalleryMovieScene)
                .load(item.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.fullScreenGalleryMovieScene)
        }
    }
}


class FullScreenGalleryPagingAdapterDiffutilsCallBack : DiffUtil.ItemCallback<Images>() {
    override fun areItemsTheSame(oldItem: Images, newItem: Images): Boolean =
        oldItem.imageUrl == newItem.imageUrl


    override fun areContentsTheSame(oldItem: Images, newItem: Images): Boolean =
        oldItem == newItem
}

class FullScreenGalleryPagingAdapterViewHolder(val binding: ItemFullScreenGalleryBinding) :
    RecyclerView.ViewHolder(binding.root)
