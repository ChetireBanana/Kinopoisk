package com.example.skillcinema.homepage.presentation

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.example.skillcinema.R
import com.example.skillcinema.application.presentation.Divider
import com.example.skillcinema.application.presentation.KEY_FIRST_LAUNCH
import com.example.skillcinema.application.presentation.LoaderErrorView
import com.example.skillcinema.application.presentation.PREFERENCES_NAME
import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.application.presentation.showBottomSheetError
import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.databinding.FragmentHomepageBinding
import com.example.skillcinema.homepage.presentation.adapters.HomepageHeaderAdapter
import com.example.skillcinema.homepage.presentation.adapters.HomepageListOfMovieCollectionAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.content.edit

@AndroidEntryPoint
class HomepageFragment : Fragment(),
    HomepageListOfMovieCollectionAdapter.HomePageClickListenerInterface,
    LoaderErrorView.LoaderErrorViewButtonsInterface {

    private lateinit var prefs: SharedPreferences


    @Inject
    lateinit var viewModelFactory: HomepageViewModelFactory
    private val viewModel: HomepageViewModel by activityViewModels { viewModelFactory }

    private var listOfFilmCollectionAdapter = HomepageListOfMovieCollectionAdapter(this)
    private var homepageHeaderAdapter = HomepageHeaderAdapter()

    private var _binding: FragmentHomepageBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initListOfFilmCollectionAdapter()
        lunchOnboardingScreen()
        stateObserver()
        dataObserver()
        errorObserver()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun errorObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.errorsFlow.collect {
                    if (it.isNotEmpty()) {
                        showBottomSheetError(it)
                    }
                }
            }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initListOfFilmCollectionAdapter() {
        val concatAdapter = ConcatAdapter(homepageHeaderAdapter, listOfFilmCollectionAdapter)
        binding.listOfFilmCollection.adapter = concatAdapter
        val dividerItemDecoration = Divider(
            bottomSpacing = requireContext().resources.getDimensionPixelSize(
                R.dimen.biggest_gap
            )
        )
        binding.listOfFilmCollection.addItemDecoration(dividerItemDecoration)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun dataObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.filmCollectionsFlow
                    .collectLatest {
                        Log.d(
                            "dataObserver",
                            "dataObserver: ${it.size}"
                        )
                        if (it.isNotEmpty()) {
                            listOfFilmCollectionAdapter.submitList(it)
                        }
                    }

            }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun stateObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.pageState
                    .collect { state ->
                        when (state) {
                            PageState.PageLoading -> {
                                Log.d("stateObserver", "PageLoading")
                                binding.loaderErrorView.visibility = View.VISIBLE
                                binding.mainScreen.visibility = View.GONE
                                Log.d(
                                    "stateObserver",
                                    "loaderErrorView ${binding.loaderErrorView.visibility} mainScreen ${binding.mainScreen.visibility}"
                                )
                            }

                            PageState.PageReady -> {
                                Log.d("stateObserver", "PageReady")
                                binding.loaderErrorView.visibility = View.GONE
                                binding.mainScreen.visibility = View.VISIBLE
                                Log.d(
                                    "stateObserver",
                                    "loaderErrorView ${binding.loaderErrorView.visibility} mainScreen ${binding.mainScreen.visibility}"
                                )
                            }

                            PageState.PageError -> {
                                Log.d("stateObserver", "PageError")
                                binding.loaderErrorView.visibility = View.VISIBLE
                                binding.mainScreen.visibility = View.GONE
                                binding.loaderErrorView.stateError()
                                binding.loaderErrorView.setOnClickListener {
                                    findNavController().navigateUp()
                                }
                                Log.d(
                                    "stateObserver",
                                    "loaderErrorView ${binding.loaderErrorView.visibility} mainScreen ${binding.mainScreen.visibility}"
                                )
                            }
                        }
                    }
            }
    }


    @SuppressLint("CommitPrefEdits")
    private fun lunchOnboardingScreen() {
        prefs = activity?.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE) ?: return
        val isItFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true)

        if (isItFirstLaunch) {
            prefs.edit() { putBoolean(KEY_FIRST_LAUNCH, false) }
            findNavController().navigate(R.id.action_homepageFragment_to_onboardingFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        viewModel.clearErrorsWhenLeaveScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    override fun onShowAllButtonClick(filmCollection: FilmCollection) {
        val action = HomepageFragmentDirections.actionHomepageFragmentToListPageFragment(
            filmCollection = filmCollection
        )
        findNavController().navigate(action)
    }

    override fun onItemClick(filmInfoDto: FilmDto) {
        val action = HomepageFragmentDirections.actionHomepageFragmentToFilmPageFragment(
            filmInfoDto.kinopoiskId
        )
        findNavController().navigate(action)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun checkIfViewed(id: Int, callback: (Boolean) -> Unit) {
        viewModel.checkIfViewed(id, callback)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRefreshButtonClick() {
        viewModel.createListOfFilmCollections()
    }

    override fun onBackButtonClick() {
        findNavController().navigateUp()
    }
}
