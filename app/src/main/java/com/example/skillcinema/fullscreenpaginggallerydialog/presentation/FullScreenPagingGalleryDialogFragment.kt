package com.example.skillcinema.fullscreenpaginggallerydialog.presentation

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.skillcinema.R
import com.example.skillcinema.databinding.DialogFullScreenGalleryBinding
import com.example.skillcinema.fullscreenpaginggallerydialog.presentation.adapters.FullScreenGalleryPagingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class FullScreenPagingGalleryDialogFragment : DialogFragment() {

    private var _binding: DialogFullScreenGalleryBinding? = null
    private val binding get() = _binding!!

    private val args: FullScreenPagingGalleryDialogFragmentArgs by navArgs()

    @Inject
    lateinit var fullScreenPagingGalleryViewModelFactory: FullScreenPagingGalleryViewModelFactory
    private val fullScreenPagingGalleryViewModel: FullScreenPagingGalleryViewModel by viewModels { fullScreenPagingGalleryViewModelFactory }

    private val adapter = FullScreenGalleryPagingAdapter()


    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogFullScreenGalleryBinding.inflate(LayoutInflater.from(context))

        val kinopoiskId = args.kinopoiskId
        Log.d("fullScreenPagingGalleryViewModel", "kinopoiskId: $kinopoiskId")
        val imagesType = args.ImagesType
        Log.d("fullScreenPagingGalleryViewModel", "imagesType: $imagesType")
        val position = args.position
        Log.d("fullScreenPagingGalleryViewModel", "position: $position")


        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setGravity(Gravity.CENTER)

        binding.fullscreenGalleryViewPager.adapter = adapter

        fullScreenPagingGalleryViewModel.getImages(kinopoiskId, imagesType).onEach {
            Log.d("getImages", it.toString())
            adapter.submitData(it)
        }.launchIn(lifecycleScope)

        adapter.addLoadStateListener { loadState ->
            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached.not()) {
                binding.fullscreenGalleryViewPager.post {
                    binding.fullscreenGalleryViewPager.currentItem = position
                }
            }
        }

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