package com.example.skillcinema.localcollectionslistpage.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.skillcinema.application.presentation.Divider
import com.example.skillcinema.databinding.FragmentLocalCollectionListPageBinding
import com.example.skillcinema.localcollectionslistpage.data.LocalCollectionListPageAdapter
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocalCollectionListPageFragment : Fragment(),
    LocalCollectionListPageAdapter.LocalCollectionListPageAdapterInterface {

    private var _binding: FragmentLocalCollectionListPageBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var profileCollectionViewModelFactory: LocalCollectionListPageViewModelFactory
    private val profileCollectionViewModel: LocalCollectionListPageViewModel by viewModels {
        profileCollectionViewModelFactory
    }

    private val localCollectionListPageAdapter = LocalCollectionListPageAdapter(this)


    private val args: LocalCollectionListPageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalCollectionListPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        collectionInfoObserver()
        filmCollectionObserver()
        listeners()

    }

    private fun collectionInfoObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            profileCollectionViewModel.getCollectionById(args.collectionId).collect { collection ->
                binding.profileCollectionTitle.text = collection.title
            }
        }
    }

    private fun listeners() {
        binding.profileCollectionBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.profileCollectionClearCollectionButton.setOnClickListener {
            profileCollectionViewModel.clearCollectionById(args.collectionId)
        }
    }

    private fun initAdapter() {
        binding.profileCollectionRecyclerView.adapter = localCollectionListPageAdapter

        val divider = Divider(
            rightSpacing = 8,
            bottomSpacing = 8
        )

        binding.profileCollectionRecyclerView.addItemDecoration(divider)
    }

    private fun filmCollectionObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            profileCollectionViewModel.getAllItemsFlowForCollection(args.collectionId)
                .collect { collection ->
                    if (collection.isNotEmpty()) {
                        binding.profileCollectionRecyclerView.visibility = View.VISIBLE
                        binding.emptyCollectionPlaceholder.visibility = View.GONE
                        localCollectionListPageAdapter.submitList(collection)
                    } else {
                        binding.profileCollectionRecyclerView.visibility = View.GONE
                        binding.emptyCollectionPlaceholder.visibility = View.VISIBLE
                    }
                }
        }
    }


    override fun onItemClick(itemId: Int) {
        val action =
            LocalCollectionListPageFragmentDirections.actionLocalCollectionListPageFragmentToFilmPageFragment(
                itemId
            )
        findNavController().navigate(action)
    }

    override fun checkIfViewed(id: Int, callback: (Boolean) -> Unit) {
        if (args.collectionId == LocalBaseCollections.VIEWED_ID) {
            callback(false)
        } else {
            return profileCollectionViewModel.checkIfViewed(id, callback)
        }
    }
}