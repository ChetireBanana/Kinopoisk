package com.example.skillcinema.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemOnboardingScreenBinding

class OnboardingAdapter(
    private val values: List<OnboardingItem>,
) : RecyclerView.Adapter<PagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val binding = ItemOnboardingScreenBinding.inflate(LayoutInflater.from(parent.context))
        return PagerViewHolder(binding)
    }

    override fun getItemCount(): Int = values.size


    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val item = values[position]
        holder.binding.onBoardingItemLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        holder.binding.message.text = item.message
        Glide
            .with(holder.binding.onboardingImageView)
            .load(item.image)
            .fitCenter()
            .into(holder.binding.onboardingImageView)
    }
}

class PagerViewHolder(val binding: ItemOnboardingScreenBinding): RecyclerView.ViewHolder(binding.root)