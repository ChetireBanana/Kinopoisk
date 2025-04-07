package com.example.skillcinema.searchfragment.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.skillcinema.R
import com.example.skillcinema.application.presentation.Divider
import com.example.skillcinema.application.presentation.showBottomSheetError
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.databinding.FragmentSearchBinding
import com.example.skillcinema.searchfragment.presentation.adapters.SearchResultPagingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(),SearchResultPagingAdapter.SearchResultPagingAdapterInterface {


    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var searchFragmentViewModelFactory: SearchFragmentViewModelFactory
    private val searchFragmentViewModel: SearchFragmentViewModel by activityViewModels { searchFragmentViewModelFactory }

    private val searchResultAdapter = SearchResultPagingAdapter (this)

    val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("SearchFragment", "onCreateView")
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SearchFragment", "onViewCreated")

        listeners()
        initView()
        initSearchPreferencesIndicator()
        initAdapter()
        searchResultObserver()
    }

    private fun searchResultObserver() {
        lifecycleScope.launch {
            searchResultAdapter.loadStateFlow.collectLatest { loadState ->
                when (loadState.refresh) {
                    is LoadState.Loading -> {
                        Log.d("SearchFragment searchResultObserver", "Loading")
                        binding.searchFragmentStartPlaceholder.visibility = View.GONE
                        binding.searchFragmentSearchResultLoadingPlaceholder.visibility =
                            View.VISIBLE
                        binding.searchFragmentSearchFailedPlaceholder.visibility = View.GONE
                    }

                    is LoadState.NotLoading -> {
                        Log.d("SearchFragment searchResultObserver", "NotLoading")
                        if (searchFragmentViewModel.isSearchPreferencesSettingsDefault()
                            && searchFragmentViewModel.keyword.isNullOrEmpty()
                        ) {
                            binding.searchFragmentStartPlaceholder.visibility = View.VISIBLE
                            binding.searchFragmentSearchResultLoadingPlaceholder.visibility =
                                View.GONE
                            binding.searchFragmentSearchFailedPlaceholder.visibility = View.GONE
                        }

                        if (searchResultAdapter.itemCount == 0) {
                            binding.searchFragmentStartPlaceholder.visibility = View.GONE
                            binding.searchFragmentSearchResultLoadingPlaceholder.visibility =
                                View.GONE
                            binding.searchFragmentSearchFailedPlaceholder.visibility = View.VISIBLE
                        } else {
                            binding.searchFragmentStartPlaceholder.visibility = View.GONE
                            binding.searchFragmentSearchResultLoadingPlaceholder.visibility =
                                View.GONE
                            binding.searchFragmentSearchFailedPlaceholder.visibility = View.GONE
                        }
                    }

                    is LoadState.Error -> {
                        Log.d("SearchFragment searchResultObserver", "Error")
                        binding.searchFragmentStartPlaceholder.visibility = View.GONE
                        binding.loaderErrorView.stateError()
                        showBottomSheetError(
                            listOf(
                                requireContext().getString(
                                    R.string.movie_api_error,
                                    getString(
                                        R.string.search_fragment_searchresults_error
                                    )
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private fun observePagedMovieCollection() {

        Log.d("SearchFragment", "observePagedMovieCollection init")
        searchFragmentViewModel.getSearchResultsPagedCollection().onEach {
            Log.d("SearchFragment", "observePagedMovieCollection onEach")
            searchResultAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun initAdapter() {
        Log.d("SearchFragment", "initAdapter")
        binding.searchFragmentSearchResultRecyclerView.adapter = searchResultAdapter

        val divider = Divider(
            bottomSpacing = 8
        )

        binding.searchFragmentSearchResultRecyclerView.addItemDecoration(divider)
    }

    private fun initSearchPreferencesIndicator() {
        val colorActive = requireContext().getColor(R.color.dark_grey)
        val colorInactive = requireContext().getColor(R.color.grey)
        if (searchFragmentViewModel.isSearchPreferencesSettingsDefault()) {
            binding.searchPreferencesButton.setColorFilter(colorInactive)
        } else {
            binding.searchPreferencesButton.setColorFilter(colorActive)
        }
    }

    private fun initView() {
        Log.d("SearchFragment", "initView init")
        if (searchFragmentViewModel.isSearchPreferencesSettingsDefault()
            && searchFragmentViewModel.keyword.isNullOrEmpty()
        ) {
            Log.d("SearchFragment", "initView if")
            binding.searchFragmentStartPlaceholder.visibility = View.VISIBLE

        } else {
            Log.d("SearchFragment", "initView else")
            binding.searchFragmentStartPlaceholder.visibility = View.GONE

            searchFragmentViewModel.keyword?.let {
                binding.searchFragmentSearchFieldEditText.setText(it)
            }
            observePagedMovieCollection()


        }
    }


    private fun listeners() {


        binding.searchPreferencesButton.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToSearchPreferencesFragment()
            findNavController().navigate(action)
        }

        binding.searchFragmentSearchFieldEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.d("afterTextChanged", "afterTextChanged")
                val searchQuery = s.toString()

                handler.removeCallbacksAndMessages(null)

                if (searchQuery.isEmpty()) {
                    Log.d("afterTextChanged", "searchQuery.isEmpty()")
                    if (!searchFragmentViewModel.isSearchPreferencesSettingsDefault()) {
                        Log.d("afterTextChanged", "not Default")

                        handler.postDelayed({
                            searchFragmentViewModel.keyword = searchQuery
                            observePagedMovieCollection()
                        }, 3000)
                    } else {
                        handler.removeCallbacksAndMessages(null)
                        Log.d("afterTextChanged", "Default")
                    }
                } else {
                    Log.d("afterTextChanged", "searchQuery not empty")
                    if (searchFragmentViewModel.keyword != searchQuery) {
                        Log.d("afterTextChanged", "keyword != searchQuery")
                        handler.removeCallbacksAndMessages(null)
                        handler.postDelayed({
                            searchFragmentViewModel.keyword = searchQuery
                            observePagedMovieCollection()
                        }, 3000)
                    } else {
                        handler.removeCallbacksAndMessages(null)
                        Log.d("afterTextChanged", "keyword == searchQuery")
                    }
                }
            }
        })
    }

    override fun onItemClick(filmDto: FilmDto) {
        val action =
            SearchFragmentDirections.actionSearchFragmentToFilmPageFragment(filmDto.kinopoiskId)
        findNavController().navigate(action)
    }
    override fun checkIfViewed(id: Int, callback: (Boolean) -> Unit) {
        searchFragmentViewModel.checkIfViewed(id, callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }



}