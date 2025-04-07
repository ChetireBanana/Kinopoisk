package com.example.skillcinema.seasonspage.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.application.presentation.Divider
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemSeasonsViewPagerScreenBinding
import com.example.skillcinema.seasonspage.data.Season

class SeasonPageViewPagerScreenAdapter : ListAdapter<Season, SeasonsPageViewHolder>(SeasonsViewPagerDiffutilsCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonsPageViewHolder {
        return SeasonsPageViewHolder(
            ItemSeasonsViewPagerScreenBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SeasonsPageViewHolder, position: Int) {
        val item = getItem(position)
        val adapter = SeasonPageSeasonsAdapter()
        holder.binding.recyclerView.adapter = adapter
        adapter.submitList(item.episodes)

        val divider = Divider(
            bottomSpacing = holder.binding.recyclerView.context.resources.getDimension(
                R.dimen.biggest_gap
            ).toInt()
        )

        holder.binding.recyclerView.addItemDecoration(divider)
    }
}

class SeasonsViewPagerDiffutilsCallback : DiffUtil.ItemCallback<Season>() {
    override fun areItemsTheSame(oldItem: Season, newItem: Season): Boolean =
        oldItem.number == newItem.number


    override fun areContentsTheSame(oldItem: Season, newItem: Season): Boolean =
        oldItem == newItem
}

class SeasonsPageViewHolder(val binding: ItemSeasonsViewPagerScreenBinding) :
    RecyclerView.ViewHolder(binding.root)
