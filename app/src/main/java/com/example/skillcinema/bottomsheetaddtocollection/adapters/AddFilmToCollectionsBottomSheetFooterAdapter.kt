package com.example.skillcinema.bottomsheetaddtocollection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.ItemAddFilmToOtherCollectionRecyclerViewFooterBinding

class AddFilmToCollectionsBottomSheetFooterAdapter(

    private val onClickListener: (Int) -> Unit

) : RecyclerView.Adapter<AddFilmToCollectionsBottomSheetFooterViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddFilmToCollectionsBottomSheetFooterViewHolder {
        return AddFilmToCollectionsBottomSheetFooterViewHolder(
            ItemAddFilmToOtherCollectionRecyclerViewFooterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(
        holder: AddFilmToCollectionsBottomSheetFooterViewHolder,
        position: Int
    ) {
        holder.binding.bottomDialogAddToCollectionFooterItem.setOnClickListener {
            onClickListener(position)
        }
    }
}

class AddFilmToCollectionsBottomSheetFooterViewHolder(val binding: ItemAddFilmToOtherCollectionRecyclerViewFooterBinding) :
    RecyclerView.ViewHolder(binding.root)
