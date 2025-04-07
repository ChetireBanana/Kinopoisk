package com.example.skillcinema.seasonspage.presentation.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemSeasonsBinding
import com.example.skillcinema.seasonspage.data.EpisodeDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SeasonPageSeasonsAdapter :
    ListAdapter<EpisodeDto, SeasonsAdapterViewHolder>(SeasonsAdapterDiffutilsCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonsAdapterViewHolder {
        return SeasonsAdapterViewHolder(
            ItemSeasonsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: SeasonsAdapterViewHolder, position: Int) {
        val item = getItem(position)

        val episodeNumber =
            holder.binding.itemSeasonSeriesTitle.context.getString(
                R.string.seasons_fragment_episode_number,
                item.episodeNumber.toString()
            )
        val episodeName =
            item.nameRu ?: item.nameEn ?: holder.binding.itemSeasonSeriesTitle.context.getString(
                R.string.episode_name_no_name
            )

        val date = if (item.releaseDate != null) {
            LocalDate.parse(item.releaseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } else {
            null
        }
        val releaseDate = date?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))


        holder.binding.itemSeasonSeriesTitle.text =
            listOf(episodeNumber, episodeName).joinToString(". ")

        if (item.releaseDate != null) {
            holder.binding.itemSeasonSeriesDescription.text = releaseDate
        }
    }
}


class SeasonsAdapterDiffutilsCallback : DiffUtil.ItemCallback<EpisodeDto>() {
    override fun areItemsTheSame(oldItem: EpisodeDto, newItem: EpisodeDto): Boolean =
        oldItem.episodeNumber == newItem.episodeNumber


    override fun areContentsTheSame(oldItem: EpisodeDto, newItem: EpisodeDto): Boolean =
        oldItem == newItem
}

class SeasonsAdapterViewHolder(val binding: ItemSeasonsBinding) :
    RecyclerView.ViewHolder(binding.root)


