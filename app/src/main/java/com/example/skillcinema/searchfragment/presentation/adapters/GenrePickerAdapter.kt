package com.example.skillcinema.searchfragment.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.data.models.Genre
import com.example.skillcinema.databinding.ItemSearchPreferencesCountryGenrePickerBinding

class GenrePickerAdapter(
    private val onClick: (Genre) -> Unit
) : ListAdapter<Genre, GenrePickerAdapterViewHolder>(GenrePickerAdapterDiffutilsCallback()) {

    private var genre: Genre? = null

    fun choseGenre(genre: Genre?, position: Int) {
        this.genre = genre
        notifyItemChanged(position)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenrePickerAdapterViewHolder {
        return GenrePickerAdapterViewHolder(
            ItemSearchPreferencesCountryGenrePickerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GenrePickerAdapterViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.countryGenrePickerTitleTextView.text = item.genre

        if (item.id == genre?.id) {
            val color = ContextCompat.getColor(
                holder.binding.itemCountryGenrePickerLayout.context,
                R.color.light_grey
            )
            val textColor = ContextCompat.getColor(
                holder.binding.itemCountryGenrePickerLayout.context,
                R.color.dark_grey
            )
            holder.binding.itemCountryGenrePickerLayout.setBackgroundColor(color)
            holder.binding.countryGenrePickerTitleTextView.setTextColor(textColor)
        } else {
            holder.binding.itemCountryGenrePickerLayout.setBackgroundColor(0)
            holder.binding.countryGenrePickerTitleTextView.setTextAppearance(R.style.MainTextStyle)
        }

        holder.binding.itemCountryGenrePickerLayout.setOnClickListener {
            onClick(item)
        }
    }
}

class GenrePickerAdapterDiffutilsCallback : DiffUtil.ItemCallback<Genre>() {
    override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean =
        oldItem == newItem
}

class GenrePickerAdapterViewHolder(val binding: ItemSearchPreferencesCountryGenrePickerBinding) :
    RecyclerView.ViewHolder(binding.root)