package com.example.skillcinema.homepage.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.application.presentation.Divider
import com.example.skillcinema.application.presentation.MAX_ELEMENT_IN_HOMEPAGE_LIST
import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.databinding.ItemMovieCollectionBinding

class HomepageListOfMovieCollectionAdapter(
    private val listener: HomePageClickListenerInterface
) : ListAdapter<FilmCollection, ListOfFilmCollectionViewHolder>(HomepageListDiffUtilCallback()),
    MovieCollectionAdapter.MovieCollectionAdapterInterface {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListOfFilmCollectionViewHolder {
        return ListOfFilmCollectionViewHolder(
            ItemMovieCollectionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ListOfFilmCollectionViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.listMoviesTittle.text = item.title
        val listMoviesAdapter = MovieCollectionAdapter(this)
        val footerAdapter = MovieCollectionFooterAdapter { listener.onShowAllButtonClick(item) }
        val concatAdapter = ConcatAdapter(listMoviesAdapter, footerAdapter)
        if (item.total >= MAX_ELEMENT_IN_HOMEPAGE_LIST) {
            holder.binding.listMoviesRecyclerView.adapter = concatAdapter
        } else {
            holder.binding.listMoviesRecyclerView.adapter = listMoviesAdapter
            holder.binding.showAllMoviesButton.visibility = View.GONE
        }
        listMoviesAdapter.setData(item.items)
        val dividerItemDecoration = Divider(
            rightSpacing = holder.binding.listMoviesRecyclerView.context.resources.getDimensionPixelSize(
                R.dimen.smallest_gap
            ),
            rightFooter = holder.binding.listMoviesRecyclerView.context.resources.getDimensionPixelSize(
                R.dimen.start_end_margin
            )
        )
        holder.binding.listMoviesRecyclerView.addItemDecoration(dividerItemDecoration)

        holder.binding.showAllMoviesButton.setOnClickListener {
            listener.onShowAllButtonClick(item)
        }
    }

    override fun onItemClick(item: FilmDto) {
        listener.onItemClick(item)
    }

    override fun checkIfViewed(id: Int, callback: (Boolean) -> Unit) {
        listener.checkIfViewed(id, callback)
    }


    interface HomePageClickListenerInterface {
        fun onShowAllButtonClick(filmCollection: FilmCollection)
        fun onItemClick(filmInfoDto: FilmDto)
        fun checkIfViewed(id: Int, callback: (Boolean) -> Unit)
    }
}

class HomepageListDiffUtilCallback : DiffUtil.ItemCallback<FilmCollection>() {
    override fun areItemsTheSame(oldItem: FilmCollection, newItem: FilmCollection): Boolean =
        oldItem.title == newItem.title

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: FilmCollection, newItem: FilmCollection): Boolean =
        oldItem == newItem
}


class ListOfFilmCollectionViewHolder(val binding: ItemMovieCollectionBinding) :
    RecyclerView.ViewHolder(binding.root)




