package com.example.skillcinema.filmlistpage.presentation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.application.presentation.Divider
import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.data.models.extras.FilmCollectionTags
import com.example.skillcinema.databinding.FragmentFilmCollectionListpageBinding
import com.example.skillcinema.homepage.presentation.adapters.MovieCollectionAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class FilmCollectionListPageFragment : Fragment(),
    MovieCollectionAdapter.MovieCollectionAdapterInterface,
    ListPagePagingAdapter.ListPagePagingAdapterInterface {

    private var _binding: FragmentFilmCollectionListpageBinding? = null
    private val binding get() = _binding!!

    private val args: FilmCollectionListPageFragmentArgs by navArgs()
    private var firstVisibleItemPosition = 0

    @Inject
    lateinit var viewModelFactory: FilmCollectionListPageViewModelFactory
    private val viewModel: FilmCollectionListPageViewModel by viewModels { viewModelFactory }

    private val listPagePagingAdapter = ListPagePagingAdapter(this)
    private val movieCollectionAdapter = MovieCollectionAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmCollectionListpageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstVisibleItemPosition = savedInstanceState?.getInt("position") ?: 0

        binding.filmCollectionTitle.text = args.filmCollection.title
        initFilmCollectionAdapterAndSetData(args.filmCollection)
        initClickListeners()

    }

    private fun initClickListeners() {
        binding.filmCollectionBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initSavePosition() {
        binding.filmCollectionRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager =
                    binding.filmCollectionRecyclerView.layoutManager as GridLayoutManager
                firstVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initFilmCollectionAdapterAndSetData(filmCollection: FilmCollection) {
        when (filmCollection.tag) {
            FilmCollectionTags.SIMPLE_COLLECTION -> {
                binding.filmCollectionRecyclerView.adapter = movieCollectionAdapter
                movieCollectionAdapter.setData(filmCollection.items)
                binding.filmCollectionRecyclerView.scrollToPosition(firstVisibleItemPosition)
                initSavePosition()
            }

            FilmCollectionTags.FILM_AND_SERIALS_BY_FILTERS_PAGED_COLLECTION -> {
                binding.filmCollectionRecyclerView.adapter = listPagePagingAdapter
                filmCollectionPagedObserver(filmCollection)
            }

            FilmCollectionTags.TOP_LIST_PAGED_COLLECTION -> {
                binding.filmCollectionRecyclerView.adapter = listPagePagingAdapter
                filmCollectionPagedObserver(filmCollection)
            }

        }
        val dividerItemDecoration = Divider(
            rightSpacing = requireContext().resources.getDimensionPixelSize(
                R.dimen.small_gap
            ),
            bottomSpacing = requireContext().resources.getDimensionPixelSize(
                R.dimen.small_gap
            ),
        )
        binding.filmCollectionRecyclerView.addItemDecoration(dividerItemDecoration)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun filmCollectionPagedObserver(collection: FilmCollection) {
        viewModel.getPagedMovieCollection(collection).onEach {
            listPagePagingAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("position", firstVisibleItemPosition)
    }


    override fun onItemClick(item: FilmDto) {
        val action = FilmCollectionListPageFragmentDirections
            .actionListPageFragmentToFilmPageFragment(item.kinopoiskId)
        findNavController().navigate(action)
    }

    override fun checkIfViewed(id: Int, callback: (Boolean) -> Unit) {
        viewModel.checkIfViewed(id, callback)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}