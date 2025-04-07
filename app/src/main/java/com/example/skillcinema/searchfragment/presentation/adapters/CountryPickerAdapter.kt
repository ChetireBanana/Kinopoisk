package com.example.skillcinema.searchfragment.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.data.models.Country
import com.example.skillcinema.databinding.ItemSearchPreferencesCountryGenrePickerBinding

class CountryPickerAdapter(
    private val onClick: (Country) -> Unit
) : ListAdapter<Country, CountryPickerAdapterViewHolder>(CountryPickerAdapterDiffutilsCallback()) {

    private var country: Country? = null

    fun choseCountry(country: Country?, position: Int) {
        this.country = country
        notifyItemChanged(position)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CountryPickerAdapterViewHolder {
        return CountryPickerAdapterViewHolder(
            ItemSearchPreferencesCountryGenrePickerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: CountryPickerAdapterViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.countryGenrePickerTitleTextView.text = item.country

        if (item.id == country?.id) {
            val color = ContextCompat.getColor(
                holder.binding.itemCountryGenrePickerLayout.context,
                R.color.light_grey
            )
            val textColor = ContextCompat.getColor(
                holder.binding.itemCountryGenrePickerLayout.context,
                R.color.dark_grey
            )
            holder.binding.itemCountryGenrePickerLayout.setBackgroundColor(color)
            holder.binding.countryGenrePickerTitleTextView.setTextColor(textColor)
        }  else {
            holder.binding.itemCountryGenrePickerLayout.setBackgroundColor(0)
            holder.binding.countryGenrePickerTitleTextView.setTextAppearance(R.style.MainTextStyle)
        }

        holder.binding.itemCountryGenrePickerLayout.setOnClickListener {
            onClick(item)
        }
    }
}

class CountryPickerAdapterDiffutilsCallback : DiffUtil.ItemCallback<Country>() {
    override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean =
        oldItem == newItem
}

class CountryPickerAdapterViewHolder(val binding: ItemSearchPreferencesCountryGenrePickerBinding) :
    RecyclerView.ViewHolder(binding.root)