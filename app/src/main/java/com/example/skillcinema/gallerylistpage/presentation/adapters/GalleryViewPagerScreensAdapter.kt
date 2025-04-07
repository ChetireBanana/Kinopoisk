package com.example.skillcinema.gallerylistpage.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.application.presentation.Divider

import com.example.skillcinema.databinding.ItemGalleryViewPagerScreenBinding

class GalleryViewPagerScreensAdapter(
    private val galleryClickListenerInterface: GalleryClickListenerInterface,
    private val pagingInterface: PagingInterface,
    private val galleryRecycledViewPool: RecyclerView.RecycledViewPool
) : ListAdapter<String, GalleryViewHolder>(GalleryViewPagerDiffUtilsCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(
            ItemGalleryViewPagerScreenBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = getItem(position)
        val adapter = GalleryImagesPagingAdapter { itemPosition -> onItemClick(itemPosition, item) }

        val divider = Divider(
            rightSpacing = 4,
            bottomSpacing = 4
        )

        holder.binding.filmographyRecyclerView.apply {
            setAdapter(adapter)
            setHasFixedSize(true)
            setRecycledViewPool(galleryRecycledViewPool)
            addItemDecoration(divider)
        }

        onPaging(adapter, item)

    }

    fun onItemClick(position: Int, imageType: String) {
        galleryClickListenerInterface.onItemClick(position, imageType)
    }

    private fun onPaging(adapter: GalleryImagesPagingAdapter, imagesType: String) {
        pagingInterface.onPaging(adapter, imagesType)
    }

    interface GalleryClickListenerInterface {
        fun onItemClick(imagePosition: Int, imageType: String)
    }

    interface PagingInterface {
        fun onPaging(adapter: GalleryImagesPagingAdapter, imagesType: String)
    }
}

class GalleryViewPagerDiffUtilsCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem


    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem
}


class GalleryViewHolder(val binding: ItemGalleryViewPagerScreenBinding) :
    RecyclerView.ViewHolder(binding.root)
