package com.example.skillcinema.searchfragment.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.ItemYearPickerViewPagerBinding


class YearPickerViewPagerAdapter(
    private val pagerId: String,
    private val yearPickerAdapterInterface: YearPickerAdapterInterface
) : RecyclerView.Adapter<YearPickerViewPagerViewHolder>() {

    private var data: MutableList<List<Int>> = mutableListOf()
    private var year: Int? = null
    private var viewWidth: Int = 0

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<List<Int>>) {
        this.data = data.toMutableList()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun choseYear(year: Int?) {
        this.year = year
        notifyDataSetChanged()
    }

    fun setViewWidth(viewWidth: Int) {
        this.viewWidth = viewWidth
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YearPickerViewPagerViewHolder {
        return YearPickerViewPagerViewHolder(
            ItemYearPickerViewPagerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: YearPickerViewPagerViewHolder, position: Int) {
        val item = data[position]

        val adapter = YearPickerAdapter { onItemClicked ->
            yearPickerAdapterInterface.onItemClicked(pagerId, onItemClicked)
        }
        holder.binding.recyclerView.adapter = adapter

        adapter.setData(item)
        adapter.choseYear(year)

    }


    interface YearPickerAdapterInterface {
        fun onItemClicked(pagerId: String, onItemClicked: Int)
    }
}

class YearPickerViewPagerViewHolder(val binding: ItemYearPickerViewPagerBinding) :
    RecyclerView.ViewHolder(binding.root)
