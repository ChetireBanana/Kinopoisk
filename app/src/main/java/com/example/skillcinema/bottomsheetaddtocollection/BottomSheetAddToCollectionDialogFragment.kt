package com.example.skillcinema.bottomsheetaddtocollection

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import com.bumptech.glide.Glide
import com.example.skillcinema.bottomsheetaddtocollection.adapters.AddFilmToCollectionsBottomSheetAdapter
import com.example.skillcinema.bottomsheetaddtocollection.adapters.AddFilmToCollectionsBottomSheetFooterAdapter
import com.example.skillcinema.databinding.BottomSheetDialogAddFilmToOtherCollectionBinding
import com.example.skillcinema.localdatabase.entities.CollectionItemEntity
import com.example.skillcinema.localdatabase.entities.LocalCollectionEntity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetAddToCollectionDialogFragment: BottomSheetDialogFragment(),
    AddFilmToCollectionsBottomSheetAdapter.AddToFilmCollectionListenerInterface {

    private var _binding: BottomSheetDialogAddFilmToOtherCollectionBinding? = null
    val binding get() = _binding!!

    @Inject
    lateinit var bottomSheetAddToCollectionViewModelFactory: BottomSheetAddToCollectionViewModelFactory
    private val bottomSheetAddToCollectionViewModel: BottomSheetAddToCollectionViewModel by activityViewModels {
        bottomSheetAddToCollectionViewModelFactory
    }

    private val args: BottomSheetAddToCollectionDialogFragmentArgs by navArgs()

    private val bottomSheetAddToCollectionMainAdapter = AddFilmToCollectionsBottomSheetAdapter(this)
    private val bottomSheetAddFilmToCollectionsBottomSheetFooterAdapter =
        AddFilmToCollectionsBottomSheetFooterAdapter { onFooterClicked ->
            onFooterClicked()
        }

    private var allCollectionsWithFilm: MutableList<CollectionItemEntity> = mutableListOf()
    private var allCollectionsFromDb: List<LocalCollectionEntity>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            BottomSheetDialogAddFilmToOtherCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BottomSheetAddToCollectionDialogFragment", "${args.filmId}")

        filmInfoObserver()
        collectionsWithFilmObserver()
        allCollectionsObserver()
        listeners()
        initAdapter()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        Log.d("BottomSheetAddToCollectionDialogFragment", "onCreateDialog")
        _binding =
            BottomSheetDialogAddFilmToOtherCollectionBinding.inflate(LayoutInflater.from(context))

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(binding.root)
        return dialog
    }

    private fun listeners() {
        binding.addFilmToOtherCollectionCloseButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun filmInfoObserver() {
        lifecycleScope.launch {
            val filmInfo = bottomSheetAddToCollectionViewModel.filmInfoObserver(args.filmId)
            Log.d("BottomSheetAddToCollectionDialogFragment", "$filmInfo")

            binding.addFilmToOtherCollectionFilmTitle.text = filmInfo.title

            binding.addFilmToOtherCollectionFilmDescription.text =
                listOfNotNull(filmInfo.year, filmInfo.genre).joinToString(", ")

            Glide
                .with(requireContext())
                .load(filmInfo.posterUrl)
                .into(binding.addFilmToOtherCollectionFilmPoster)
        }
    }

    private fun collectionsWithFilmObserver() {
        lifecycleScope.launch {
            bottomSheetAddToCollectionViewModel.collectionsWithFilmObserver(args.filmId).collect {
                allCollectionsWithFilm = it.toMutableList()
                Log.d("BottomSheetAddToCollectionDialogFragment", "${it.size}")
            }
        }
    }


    private fun allCollectionsObserver() {
        lifecycleScope.launch {
            bottomSheetAddToCollectionViewModel.allCollectionsObserver().collect { allCollections ->
                allCollectionsFromDb = allCollections
                Log.d("BottomSheetAddToCollectionDialogFragment", "${allCollections.size}")
                bottomSheetAddToCollectionMainAdapter.submitList(allCollections)

            }
        }
    }

    private fun initAdapter() {
        val concatAdapter = ConcatAdapter(
            bottomSheetAddToCollectionMainAdapter,
            bottomSheetAddFilmToCollectionsBottomSheetFooterAdapter
        )

        binding.addFilmToOtherCollectionRecyclerView.adapter = concatAdapter
    }


    private fun onFooterClicked() {
        val action = BottomSheetAddToCollectionDialogFragmentDirections.actionBottomSheetAddToCollectionDialogFragmentToCreateCollectionDialogFragment()

        findNavController().navigate(action)
    }

    override fun checkCollection(collectionId: Int, isChecked: Boolean) {
        lifecycleScope.launch{
            bottomSheetAddToCollectionViewModel.checkCollectionWithFilm(collectionId, args.filmId, isChecked)
        }
    }

    override fun isFilmInCollection(collectionId: Int): Boolean {
        return allCollectionsWithFilm.any { it.collectionId == collectionId }
    }
}