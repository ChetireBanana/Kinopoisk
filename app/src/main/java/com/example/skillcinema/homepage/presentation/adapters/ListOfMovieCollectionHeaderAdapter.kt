package com.example.skillcinema.homepage.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.ItemHeaderSkillcinemaLogoBinding

class HomepageHeaderAdapter : RecyclerView.Adapter<HomepageFooterAdapterViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomepageFooterAdapterViewHolder {
        return HomepageFooterAdapterViewHolder(
            ItemHeaderSkillcinemaLogoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: HomepageFooterAdapterViewHolder, position: Int) {
    }
}

class HomepageFooterAdapterViewHolder(val binding: ItemHeaderSkillcinemaLogoBinding) :
    RecyclerView.ViewHolder(binding.root)