package com.example.skillcinema.profilefragment.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.ItemLocalBaseCollectionsFooterBinding


class InterestingItemsFooterAdapter(
    private val profileCollectionId: Int,
    private val onClickListener: (Int) -> Unit
) : RecyclerView.Adapter<InterestingCollectionFooterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InterestingCollectionFooterViewHolder {
        return InterestingCollectionFooterViewHolder(
            ItemLocalBaseCollectionsFooterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: InterestingCollectionFooterViewHolder, position: Int) {
        holder.binding.itemLocalBaseCollectionFooterClearHistoryButton.setOnClickListener {
            onClickListener(profileCollectionId)
        }
    }


}

class InterestingCollectionFooterViewHolder(val binding: ItemLocalBaseCollectionsFooterBinding) :
    RecyclerView.ViewHolder(binding.root)