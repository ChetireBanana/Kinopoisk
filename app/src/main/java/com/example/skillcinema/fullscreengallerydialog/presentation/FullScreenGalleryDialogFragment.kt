package com.example.skillcinema.fullscreengallerydialog.presentation

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.skillcinema.R
import com.example.skillcinema.databinding.DialogFullScreenGalleryBinding
import com.example.skillcinema.fullscreengallerydialog.presentation.adapters.FullScreenGalleryViewPagerScreenAdapter
import kotlin.math.abs

class FullScreenGalleryDialogFragment : DialogFragment() {

    private var _binding: DialogFullScreenGalleryBinding? = null
    private val binding get() = _binding!!

    private val args: FullScreenGalleryDialogFragmentArgs by navArgs()

    private val adapter = FullScreenGalleryViewPagerScreenAdapter()


    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogFullScreenGalleryBinding.inflate(LayoutInflater.from(context))

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setGravity(Gravity.CENTER)

        val images = args.imagesCollection.items
        binding.fullscreenGalleryViewPager.adapter = adapter
        adapter.submitList(images)
        binding.fullscreenGalleryViewPager.currentItem = args.position

        binding.fullscreenGalleryViewPager.offscreenPageLimit = 3
        binding.fullscreenGalleryViewPager.clipToPadding = false
        binding.fullscreenGalleryViewPager.clipChildren = false

        binding.closeButton.setOnClickListener {
            dialog?.dismiss()
        }

        setUpTransformer()

        return AlertDialog.Builder(requireActivity(), R.style.GalleryDialogStyle)
            .setView(binding.root)
            .create()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val childCount = binding.fullscreenGalleryViewPager.childCount
            binding.fullscreenGalleryViewPager.setOffscreenPageLimit(childCount)
        } else {
            binding.fullscreenGalleryViewPager.setOffscreenPageLimit(3)
        }
    }


    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }
        binding.fullscreenGalleryViewPager.setPageTransformer(transformer)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}

