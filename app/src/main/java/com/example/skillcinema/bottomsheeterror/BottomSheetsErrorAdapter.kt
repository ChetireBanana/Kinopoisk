package com.example.skillcinema.bottomsheeterror

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.ItemBottomSheetBinding

class BottomSheetsErrorAdapter :
    ListAdapter<String, BottomSheetViewHolder>(BottomSheetDiffutilsCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        return BottomSheetViewHolder(
            ItemBottomSheetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.message.text = item
    }
}

class BottomSheetDiffutilsCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem


    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem
}

class BottomSheetViewHolder(val binding: ItemBottomSheetBinding) :
    RecyclerView.ViewHolder(binding.root)