package com.example.skillcinema.filmography.presentation

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.skillcinema.R
import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.application.presentation.professionKeyTranslator
import com.example.skillcinema.application.presentation.showBottomSheetError
import com.example.skillcinema.data.models.PersonInfoFilmDto
import com.example.skillcinema.data.models.PersonInfoFilmDtoCollection
import com.example.skillcinema.databinding.FragmentFilmographyListpageBinding
import com.example.skillcinema.filmography.presentation.adapters.FilmographyScreensAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FilmographyListPageFragment : Fragment(),
    FilmographyScreensAdapter.FilmographyClickListenerInterface {

    private var _binding: FragmentFilmographyListpageBinding? = null
    val binding get() = _binding!!

    private val filmographyScreensAdapter = FilmographyScreensAdapter(this)

    private val args: FilmographyListPageFragmentArgs by navArgs()

    private var isAdditionalInformationLoaded = false

    @Inject
    lateinit var filmographyViewModelFactory: FilmographyViewModelFactory
    private val viewModel: FilmographyViewModel by viewModels { filmographyViewModelFactory }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmographyListpageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateObserver()
        mainFilmographyObserver()
        additionalFilmographyObserver()
        errorObserver()
        personNameObserver()
        getFilmography()
        clickListeners()

    }


    private fun stateObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.filmographyPageStateFlow
                    .collect { state ->
                        when (state) {
                            PageState.PageLoading -> {
                                binding.filmographyListPageLoadErrorView.visibility = View.VISIBLE
                                binding.filmographyListPageMainScreen.visibility = View.GONE
                            }

                            PageState.PageReady -> {
                                binding.filmographyListPageLoadErrorView.visibility = View.GONE
                                binding.filmographyListPageMainScreen.visibility = View.VISIBLE
                            }

                            PageState.PageError -> {
                                binding.filmographyListPageLoadErrorView.visibility = View.VISIBLE
                                binding.filmographyListPageMainScreen.visibility = View.GONE
                                binding.filmographyListPageLoadErrorView.stateError()
                                binding.filmographyListPageLoadErrorView.setOnClickListener {
                                    findNavController().navigateUp()
                                }
                            }
                        }
                    }
            }
    }


    private fun mainFilmographyObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.mainFilmographyFlow.collect { mainList ->
                    if (mainList.isNotEmpty()) {
                        if (!isAdditionalInformationLoaded) {
                            val filmListPrepared = createListPersonInfoFilmDtoCollection(mainList)
                            initAdapterAndTabs(filmListPrepared)
                        }
                    }
                }
            }
    }

    private fun additionalFilmographyObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.additionalFilmographyFlow.collect { additionalList ->
                    if (additionalList.isNotEmpty()) {
                        isAdditionalInformationLoaded = true
                        val filmListPrepared = createListPersonInfoFilmDtoCollection(additionalList)
                        initAdapterAndTabs(filmListPrepared)
                    }
                }
            }
    }

    private fun personNameObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.getActorName()
                viewModel.actorNameFlow.collect { name ->
                    binding.actorName.text = name
                }
            }
    }

    private fun errorObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.filmographyErrorFlow.collect { errorList ->
                    if (errorList.isNotEmpty()) {
                        showBottomSheetError(errorList)
                    }
                }
            }
    }


    private fun getFilmography() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.getFilmography(args.personId)
            }
    }

    private fun initAdapterAndTabs(filmList: List<PersonInfoFilmDtoCollection>) {

        Log.d("initAdapterAndTabs", "initAdapterAndTabs filmList ${filmList.size}")

        binding.filmographyListPageViewPager.adapter = filmographyScreensAdapter
        filmographyScreensAdapter.submitList(filmList)

        TabLayoutMediator(
            binding.filmographyTabLayout,
            binding.filmographyListPageViewPager
        ) { tab, position ->
            val tabName = professionKeyTranslator(
                requireContext(),
                filmList[position].title
            )
            val tabQuantity =
                filmList[position].films.size.toString()
            val tabText = SpannableString(listOf(tabName, tabQuantity).joinToString("   "))
            tabText.setSpan(
                RelativeSizeSpan(0.7f),
                tabName.length,
                tabText.length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            tabText.setSpan(
                ForegroundColorSpan(requireContext().getColor(R.color.light_grey)),
                tabName.length,
                tabText.length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            tab.text = tabText
        }.attach()
    }

    private fun createListPersonInfoFilmDtoCollection(filmography: List<PersonInfoFilmDto>): List<PersonInfoFilmDtoCollection> {
        val listPersonInfoFilmDtoCollection: MutableList<PersonInfoFilmDtoCollection> =
            mutableListOf()

        val variants = listOf(
            "WRITER",
            "OPERATOR",
            "EDITOR",
            "COMPOSER",
            "PRODUCER_USSR",
            "HIMSELF",
            "HERSELF",
            "HRONO_TITR_MALE",
            "HRONO_TITR_FEMALE",
            "TRANSLATOR",
            "DIRECTOR",
            "DESIGN",
            "PRODUCER",
            "ACTOR",
            "VOICE_DIRECTOR",
            "UNKNOWN"
        )
        variants.forEach { variant ->
            val filteredCollection = filmography.filter { it.professionKey == variant }
            if (filteredCollection.isNotEmpty()) {
                listPersonInfoFilmDtoCollection.add(
                    PersonInfoFilmDtoCollection(
                        variant,
                        filteredCollection
                    )
                )
            }
        }
        return listPersonInfoFilmDtoCollection
    }


    private fun clickListeners() {
        binding.filmographyListPageBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onItemClick(personInfoFilmDto: PersonInfoFilmDto) {
        val action =
            FilmographyListPageFragmentDirections.actionFilmographyListPageFragmentToFilmPageFragment(
                personInfoFilmDto.filmId
            )
        findNavController().navigate(action)
    }

    override fun checkIfViewed(itemId: Int, callback: (Boolean) -> Unit) {
        viewModel.checkIfViewed(itemId, callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}