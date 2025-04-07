package com.example.skillcinema.profilefragment.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemLocalCollectionsBinding
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.localdatabase.entities.LocalCollectionEntity


class LocalCollectionsAdapter(
    private val localCollectionsClickListener: LocalCollectionsClickListener
) : ListAdapter<LocalCollectionEntity, LocalCollectionsAdapterViewHolder>(LocalCollectionsDiffUtils()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocalCollectionsAdapterViewHolder {
        return LocalCollectionsAdapterViewHolder(
            ItemLocalCollectionsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LocalCollectionsAdapterViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.localCollectionTitle.text = item.title
        holder.binding.localCollectionSizeIndicator.text = item.size.toString()

        if (item.collectionId == LocalBaseCollections.FAVOURITE_ID || item.collectionId == LocalBaseCollections.BOOKMARK_ID) {
            holder.binding.deleteLocalCollectionButton.visibility = View.GONE
        }

        Glide
            .with(holder.binding.localCollectionLayout)
            .load(item.icon)
            .into(holder.binding.localCollectionIcon)

        holder.binding.localCollectionLayout.setOnClickListener {
            localCollectionsClickListener.onLocalCollectionClick(item.collectionId)
        }

        holder.binding.deleteLocalCollectionButton.setOnClickListener {
            localCollectionsClickListener.onDeleteButtonCollectionClick(item.collectionId)
        }
    }
}

interface LocalCollectionsClickListener {
    fun onLocalCollectionClick(collectionId: Int)
    fun onDeleteButtonCollectionClick(collectionId: Int)
}

class LocalCollectionsDiffUtils : DiffUtil.ItemCallback<LocalCollectionEntity>() {
    override fun areItemsTheSame(
        oldItem: LocalCollectionEntity,
        newItem: LocalCollectionEntity
    ): Boolean =
        oldItem.collectionId == newItem.collectionId

    override fun areContentsTheSame(
        oldItem: LocalCollectionEntity,
        newItem: LocalCollectionEntity
    ): Boolean =
        oldItem == newItem

}

class LocalCollectionsAdapterViewHolder(val binding: ItemLocalCollectionsBinding) :
    RecyclerView.ViewHolder(binding.root)