package com.example.skillcinema.bottomsheetaddtocollection.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.ItemAddFilmToOtherCollectionRecyclerViewBinding
import com.example.skillcinema.localdatabase.entities.LocalCollectionEntity


class AddFilmToCollectionsBottomSheetAdapter(
    private val addToFilmCollectionListener: AddToFilmCollectionListenerInterface
) : ListAdapter<LocalCollectionEntity, AddFilmToCollectionsBottomSheetViewHolder>(
    AddFilmToCollectionsBottomSheetDiffutils()
) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddFilmToCollectionsBottomSheetViewHolder {
        return AddFilmToCollectionsBottomSheetViewHolder(
            ItemAddFilmToOtherCollectionRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: AddFilmToCollectionsBottomSheetViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        Log.d("AddFilmToCollectionsBottomSheetAdapter", item.title)

        holder.binding.bottomDialogAddToCollectionSize.text = item.size.toString()
        holder.binding.bottomDialogAddToCollectionTitle.text = item.title

        holder.binding.bottomDialogAddToCollectionCheckBox.isChecked =
            addToFilmCollectionListener.isFilmInCollection(item.collectionId)

        holder.binding.bottomDialogAddToCollectionCheckBox.setOnCheckedChangeListener { _, isChecked ->
            addToFilmCollectionListener.checkCollection(item.collectionId, isChecked)
        }
    }

    interface AddToFilmCollectionListenerInterface {
        fun checkCollection(collectionId: Int, isChecked: Boolean)
        fun isFilmInCollection(collectionId: Int): Boolean
    }
}

class AddFilmToCollectionsBottomSheetDiffutils : DiffUtil.ItemCallback<LocalCollectionEntity>() {
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


class AddFilmToCollectionsBottomSheetViewHolder(val binding: ItemAddFilmToOtherCollectionRecyclerViewBinding) :
    RecyclerView.ViewHolder(binding.root)


