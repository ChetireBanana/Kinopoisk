package com.example.skillcinema.filmpage.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.application.presentation.MAX_ELEMENT_IN_FILM_PAGE_GALLERY_LIST
import com.example.skillcinema.gallerylistpage.data.Images
import com.example.skillcinema.databinding.ItemImagesFilmpageBinding


class FilmPageGalleryAdapter(
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<FilmPageGalleryViewHolder>() {

    private var data: MutableList<Images> = mutableListOf()
    private val maxSize = MAX_ELEMENT_IN_FILM_PAGE_GALLERY_LIST

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Images>) {
        this.data = data.toMutableList()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmPageGalleryViewHolder {
        return FilmPageGalleryViewHolder(
            ItemImagesFilmpageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return if (data.size < maxSize) {
            data.size
        } else {
            maxSize
        }
    }


    override fun onBindViewHolder(holder: FilmPageGalleryViewHolder, position: Int) {
        val item = data[position]
        Glide
            .with(holder.binding.movieScene)
            .load(item.previewUrl)
            .into(holder.binding.movieScene)

        holder.binding.movieScene.setOnClickListener {
            onItemClick(position)
        }
    }
}

class FilmPageGalleryViewHolder(val binding: ItemImagesFilmpageBinding) :
    RecyclerView.ViewHolder(binding.root)