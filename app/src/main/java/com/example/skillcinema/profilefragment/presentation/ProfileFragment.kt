package com.example.skillcinema.profilefragment.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.example.skillcinema.R
import com.example.skillcinema.application.di.App
import com.example.skillcinema.application.presentation.Divider
import com.example.skillcinema.databinding.FragmentProfileBinding
import com.example.skillcinema.localdatabase.dao.CommonDao
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.localdatabase.data.LocalItemType
import com.example.skillcinema.localdatabase.entities.LocalItemEntity
import com.example.skillcinema.profilefragment.presentation.adapters.InterestingFilmsAdapter
import com.example.skillcinema.profilefragment.presentation.adapters.InterestingFilmsAdapter.ProfileFragmentCollectionAdapterInterface
import com.example.skillcinema.profilefragment.presentation.adapters.InterestingItemsFooterAdapter
import com.example.skillcinema.profilefragment.presentation.adapters.LocalCollectionsAdapter
import com.example.skillcinema.profilefragment.presentation.adapters.LocalCollectionsClickListener
import com.example.skillcinema.profilefragment.presentation.adapters.ProfileFragmentViewedCollectionAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(), LocalCollectionsClickListener,
    ProfileFragmentCollectionAdapterInterface {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var profileViewModelFactory: ProfileViewModelFactory
    private val profileViewModel: ProfileViewModel by viewModels { profileViewModelFactory }

    private val interestingItemsAdapter =
        InterestingFilmsAdapter(this)
    private val interestingItemsFooterAdapter = InterestingItemsFooterAdapter(
        LocalBaseCollections.INTERESTING_ID,
        ::onClearCollectionButtonClick
    )

    private val viewedCollectionAdapter =
        ProfileFragmentViewedCollectionAdapter { onItemClicked -> onItemClick(onItemClicked) }
    private val viewedCollectionFooterAdapter = InterestingItemsFooterAdapter(
        LocalBaseCollections.VIEWED_ID,
        ::onClearCollectionButtonClick
    )

    private val otherLocalCollectionsAdapter =
        LocalCollectionsAdapter(this)

    private lateinit var commonDao: CommonDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        commonDao = App.database.commonDao()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()
        initOtherLocalCollectionsAdapter()
        interestingItemsObserver()
        viewedCollectionObserver()
        otherCollectionsObserver()
        clickListeners()

    }


    private fun initAdapters() {
        val interestingFilmsConcatAdapter =
            ConcatAdapter(interestingItemsAdapter, interestingItemsFooterAdapter)
        binding.interestingCollectionRecyclerView.adapter = interestingFilmsConcatAdapter

        val viewedFilmsConcatAdapter =
            ConcatAdapter(viewedCollectionAdapter, viewedCollectionFooterAdapter)
        binding.viewedCollectionRecyclerView.adapter = viewedFilmsConcatAdapter

        binding.otherLocalCollectionsRecyclerView.adapter = otherLocalCollectionsAdapter

        val interestingViewedCollectionsDivider = Divider(
            rightSpacing = 8,
            bottomSpacing = 8,
        )


        binding.interestingCollectionRecyclerView.addItemDecoration(
            interestingViewedCollectionsDivider
        )
        binding.viewedCollectionRecyclerView.addItemDecoration(interestingViewedCollectionsDivider)
    }

    private fun initOtherLocalCollectionsAdapter() {
        binding.otherLocalCollectionsRecyclerView.adapter = otherLocalCollectionsAdapter

        val layoutSize = binding.otherLocalCollectionsLayout.width
        val itemSize =
            requireContext().resources.getDimension(R.dimen.local_collection_item_size).toInt()
        val spanCount = layoutSize / itemSize
        val freeSpace = layoutSize - (itemSize * spanCount)
        val spacingCount = spanCount - 1
        val spacing = freeSpace / spacingCount

        val divider = Divider(
            rightSpacing = spacing,
            bottomSpacing = spacing,
        )

        binding.otherLocalCollectionsRecyclerView.addItemDecoration(divider)
    }


    private fun interestingItemsObserver() {
        lifecycleScope.launch {
            profileViewModel.getInterestingItemsCollection().collect { interestingFilms ->
                if (interestingFilms.isNotEmpty()) {
                    binding.interestingCollectionLayout.visibility = View.VISIBLE
                    interestingItemsAdapter.submitList(interestingFilms)
                } else {
                    binding.interestingCollectionLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun viewedCollectionObserver() {
        lifecycleScope.launch {
            profileViewModel.getViewedCollection().collect { interestingFilms ->
                if (interestingFilms.isNotEmpty()) {
                    binding.viewedCollectionLayout.visibility = View.VISIBLE
                    viewedCollectionAdapter.submitList(interestingFilms)
                } else {
                    binding.viewedCollectionLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun otherCollectionsObserver() {
        lifecycleScope.launch {
            profileViewModel.getOtherLocalCollectionsFromDB().collect { collections ->
                Log.d("Profile fragment", "collections quantity: ${collections.size}")
                otherLocalCollectionsAdapter.submitList(collections)
            }
        }
    }


    override fun onItemClick(item: LocalItemEntity) {
        when (item.type) {
            LocalItemType.FILM -> {
                val action =
                    ProfileFragmentDirections.actionProfileFragmentToFilmPageFragment(item.itemId)
                findNavController().navigate(action)
            }

            LocalItemType.PERSON -> {
                val action =
                    ProfileFragmentDirections.actionProfileFragmentToPersonalPageFragment(item.itemId)
                findNavController().navigate(action)
            }
        }
    }

    override fun checkIfViewed(id: Int, callback: (Boolean) -> Unit) {
        profileViewModel.checkIfViewed(id, callback)
    }

    private fun clickListeners() {
        binding.viewedCollectionShowAllMoviesButton.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToLocalCollectionListPageFragment(
                    LocalBaseCollections.VIEWED_ID
                )
            findNavController().navigate(action)
        }

        binding.interestingShowAllMoviesButton.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToLocalCollectionListPageFragment(
                    LocalBaseCollections.INTERESTING_ID
                )
            findNavController().navigate(action)
        }

        binding.createCollectionButton.setOnClickListener {

            val action =
                ProfileFragmentDirections.actionProfileFragmentToCreateCollectionDialogFragment()
            findNavController().navigate(action)
        }
    }

    private fun onClearCollectionButtonClick(collectionId: Int) {
        profileViewModel.clearCollection(collectionId)
    }

    override fun onLocalCollectionClick(collectionId: Int) {
        val action =
            ProfileFragmentDirections.actionProfileFragmentToLocalCollectionListPageFragment(
                collectionId
            )
        findNavController().navigate(action)
    }

    override fun onDeleteButtonCollectionClick(collectionId: Int) {
        profileViewModel.deleteCollectionFromDB(collectionId)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}