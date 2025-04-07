package com.example.skillcinema.filmography.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.application.presentation.Divider
import com.example.skillcinema.data.models.PersonInfoFilmDto
import com.example.skillcinema.data.models.PersonInfoFilmDtoCollection
import com.example.skillcinema.databinding.ItemFilmographyListPageViewPagerScreenBinding
import com.example.skillcinema.filmography.presentation.adapters.FilmographyPersonFilmListAdapter.FilmographyPersonFilmListInterface

class FilmographyScreensAdapter(
    private val filmographyClickListenerInterface: FilmographyClickListenerInterface
) : ListAdapter<PersonInfoFilmDtoCollection, FilmographyPagerViewHolder>(
    FilmographyScreensDiffutilsCallback()
), FilmographyPersonFilmListInterface {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmographyPagerViewHolder {

        return FilmographyPagerViewHolder(
            ItemFilmographyListPageViewPagerScreenBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: FilmographyPagerViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("FilmographyScreensAdapter", "onBindViewHolder item ${item.title}")
        val adapter =
            FilmographyPersonFilmListAdapter(this)
        holder.binding.filmographyListPageViewPagerScreenRecyclerView.adapter = adapter
        adapter.submitList(item.films)
        val divider = Divider(
            bottomSpacing = holder.binding.root.context.resources.getDimension(R.dimen.smallest_gap)
                .toInt()
        )
        holder.binding.filmographyListPageViewPagerScreenRecyclerView.addItemDecoration(divider)
    }

    override fun onItemClick(item: PersonInfoFilmDto) {
        filmographyClickListenerInterface.onItemClick(item)
    }

    override fun checkIfViewed(itemId: Int, callback: (Boolean) -> Unit) {
        filmographyClickListenerInterface.checkIfViewed(itemId, callback)
    }

    interface FilmographyClickListenerInterface {
        fun onItemClick(personInfoFilmDto: PersonInfoFilmDto)
        fun checkIfViewed(itemId: Int, callback: (Boolean) -> Unit)
    }
}

class FilmographyScreensDiffutilsCallback : DiffUtil.ItemCallback<PersonInfoFilmDtoCollection>() {
    override fun areItemsTheSame(
        oldItem: PersonInfoFilmDtoCollection,
        newItem: PersonInfoFilmDtoCollection
    ): Boolean =
        oldItem.title == newItem.title

    override fun areContentsTheSame(
        oldItem: PersonInfoFilmDtoCollection,
        newItem: PersonInfoFilmDtoCollection
    ): Boolean =
        oldItem == newItem

}

class FilmographyPagerViewHolder(val binding: ItemFilmographyListPageViewPagerScreenBinding) :
    RecyclerView.ViewHolder(binding.root)
