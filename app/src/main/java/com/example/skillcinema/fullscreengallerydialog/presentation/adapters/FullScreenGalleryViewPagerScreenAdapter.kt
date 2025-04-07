package com.example.skillcinema.fullscreengallerydialog.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemFullScreenGalleryBinding
import com.example.skillcinema.gallerylistpage.data.Images


class FullScreenGalleryViewPagerScreenAdapter :
    ListAdapter<Images, FullScreenGalleryViewPagerScreenAdapterViewHolder>(
        FullScreenGalleryViewPagerScreenAdapterDiffutilsCallback()
    ) {

    private var landscapeOrientation = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FullScreenGalleryViewPagerScreenAdapterViewHolder {
        return FullScreenGalleryViewPagerScreenAdapterViewHolder(
            ItemFullScreenGalleryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: FullScreenGalleryViewPagerScreenAdapterViewHolder,
        position: Int
    ) {
        val item = getItem(position)

        if (landscapeOrientation) {
            holder.binding.fullScreenGalleryMovieScene
        }

        Glide
            .with(holder.binding.fullScreenGalleryMovieScene)
            .load(item.imageUrl)
            .into(holder.binding.fullScreenGalleryMovieScene)
    }
}

class FullScreenGalleryViewPagerScreenAdapterDiffutilsCallback :
    DiffUtil.ItemCallback<Images>() {
    override fun areItemsTheSame(oldItem: Images, newItem: Images): Boolean =
        oldItem.imageUrl == newItem.imageUrl


    override fun areContentsTheSame(oldItem: Images, newItem: Images): Boolean =
        oldItem == newItem


}

class FullScreenGalleryViewPagerScreenAdapterViewHolder(val binding: ItemFullScreenGalleryBinding) :
    RecyclerView.ViewHolder(binding.root)

