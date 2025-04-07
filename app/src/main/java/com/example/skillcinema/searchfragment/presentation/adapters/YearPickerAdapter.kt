package com.example.skillcinema.searchfragment.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemYearPickerBinding

class YearPickerAdapter(
    private val onClick: (Int) -> Unit,
) : RecyclerView.Adapter<YearPickerAdapterViewHolder>() {

    private var data = mutableListOf<Int>()
    private var year: Int? = null


    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Int>) {
        this.data = data.toMutableList()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun choseYear(year: Int?) {
        this.year = year
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearPickerAdapterViewHolder {
        return YearPickerAdapterViewHolder(
            ItemYearPickerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: YearPickerAdapterViewHolder, position: Int) {
        val item = data[position]

        holder.binding.year.text = item.toString()
        if (item == year) {
            val color = ContextCompat.getColor(
                holder.binding.itemYearPickerLayout.context,
                R.color.light_grey
            )
            holder.binding.itemYearPickerLayout.setBackgroundColor(color)
        } else {
            holder.binding.itemYearPickerLayout.setBackgroundColor(0)
        }
        holder.binding.itemYearPickerLayout.setOnClickListener {
            onClick(item)
        }
    }
}

class YearPickerAdapterViewHolder(val binding: ItemYearPickerBinding) :
    RecyclerView.ViewHolder(binding.root)
