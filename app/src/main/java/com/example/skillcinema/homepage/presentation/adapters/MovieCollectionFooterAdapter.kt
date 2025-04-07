package com.example.skillcinema.homepage.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.ItemMovieCollectionFooterBinding

class MovieCollectionFooterAdapter(
    private val onClickListener: (Int) -> Unit
) : RecyclerView.Adapter<MovieCollectionFooterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieCollectionFooterViewHolder {
        return MovieCollectionFooterViewHolder(
            ItemMovieCollectionFooterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = 1


    override fun onBindViewHolder(holder: MovieCollectionFooterViewHolder, position: Int) {
        holder.binding.itemMovieCollectionFooterButton.setOnClickListener {
            onClickListener(position)
        }

    }
}


class MovieCollectionFooterViewHolder(val binding: ItemMovieCollectionFooterBinding) :
    RecyclerView.ViewHolder(binding.root)