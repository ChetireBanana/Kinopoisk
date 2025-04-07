package com.example.skillcinema.filmpage.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.data.models.StaffDto
import com.example.skillcinema.databinding.ItemFilmCrewBinding

class FilmCrewListAdapter(
    private val onItemClick: (StaffDto) -> Unit
) : RecyclerView.Adapter<FilmCrewListViewHolder>() {

    private var data: MutableList<StaffDto> = mutableListOf()
    private var maxSize: Int = 100000
    private var adoptToListPage: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    fun setDate(data: List<StaffDto>) {
        this.data = data.toMutableList()
        notifyDataSetChanged()
    }

    fun setMaxSize(maxSize: Int) {
        this.maxSize = maxSize
    }

    fun adoptToListPage() {
        adoptToListPage = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmCrewListViewHolder {
        return FilmCrewListViewHolder(
            ItemFilmCrewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = if (data.size < maxSize) data.size else maxSize


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FilmCrewListViewHolder, position: Int) {
        val item = data[position]

        holder.binding.name.text = if (
            !item.nameRu.isNullOrEmpty()
        ) item.nameRu else item.nameEn ?: "Безымянный"

        if (item.professionKey == "ACTOR") {
            holder.binding.role.text = item.description ?: "Эпизодическачя роль"
        } else {
            val professionDescription = listOfNotNull(
                item.professionText.dropLast(1),
                item.description
            ).joinToString(" ")
            holder.binding.role.text = professionDescription
        }
        Glide
            .with(holder.binding.photo)
            .load(item.posterUrl)
            .into(holder.binding.photo)

        if (adoptToListPage) {
            holder.binding.photo.layoutParams.height =
                holder.binding.photo.resources.getDimension(R.dimen.image_height).toInt()
            holder.binding.photo.layoutParams.width =
                holder.binding.photo.resources.getDimension(R.dimen.image_width).toInt()
            holder.binding.crewItem.layoutParams.width = LayoutParams.MATCH_PARENT
        }

        holder.binding.crewItem.setOnClickListener {
            onItemClick(item)
        }
    }
}

class FilmCrewListViewHolder(val binding: ItemFilmCrewBinding) :
    RecyclerView.ViewHolder(binding.root)