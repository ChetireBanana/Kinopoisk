package com.example.skillcinema.searchfragment.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.R
import com.example.skillcinema.data.models.extras.FiltersOrder
import com.example.skillcinema.data.models.extras.FiltersTypesOfFilms
import com.example.skillcinema.databinding.FragmentSearchPreferencesBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchPreferencesFragment : Fragment() {

    private var _binding: FragmentSearchPreferencesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var searchFragmentViewModelFactory: SearchFragmentViewModelFactory
    private val viewModel: SearchFragmentViewModel by activityViewModels { searchFragmentViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchPreferencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("SearchFragment", "SearchPreferencesFragment init")

        initView()
        clickListeners()
    }


    private fun initView() {
        Log.d("SearchFragment", "SPinitView init")

        initFilmTypesFilterToggleGroup()
        initCountryIndicator()
        initGenreIndicator()
        initYearIndicator()
        initRatingSlider()
        initSortOrderToggleGroup()
        initHaveSeenSwitch()
        resetPreferencesButtonActivator()
    }

    private fun initFilmTypesFilterToggleGroup() {
        when (viewModel.filterOptionsSaved.type) {
            FiltersTypesOfFilms.ALL -> {
                binding.filmTypesToggleGroup.check(R.id.all_types_toggle)
            }

            FiltersTypesOfFilms.FILM -> {
                binding.filmTypesToggleGroup.check(R.id.films_types_toggle)
            }

            FiltersTypesOfFilms.TV_SERIES -> {
                binding.filmTypesToggleGroup.check(R.id.serials_types_toggle)
            }
        }
    }


    private fun initCountryIndicator() {
        binding.countryIndicator.text = if (viewModel.filterOptionsSaved.countries != null) {
            viewModel.filterOptionsSaved.countries!!.country
        } else {
            getString(R.string.search_preferences_country_any)
        }
    }

    private fun initGenreIndicator() {
        binding.genreIndicator.text = if (viewModel.filterOptionsSaved.genres != null) {
            viewModel.filterOptionsSaved.genres!!.genre
        } else {
            getString(R.string.share_preferences_genre_any)
        }
    }

    private fun initYearIndicator() {
        val yearFrom = if (viewModel.filterOptionsSaved.yearFrom != null) {
            "c ${viewModel.filterOptionsSaved.yearFrom}"
        } else {
            null
        }
        val yearTo = if (viewModel.filterOptionsSaved.yearTo != null) {
            "до ${viewModel.filterOptionsSaved.yearTo}"
        } else {
            null
        }
        val yearIndicator = if (yearFrom != null || yearTo != null) {
            listOfNotNull(yearFrom, yearTo).joinToString(" ")
        } else {
            getString(R.string.search_preferences_year_indicator_any)
        }
        binding.yearIndicator.text = yearIndicator
    }

    private fun initRatingSlider() {
        val ratingFrom = viewModel.filterOptionsSaved.ratingFrom
        val ratingTo = viewModel.filterOptionsSaved.ratingTo
        binding.ratingSlider.setValues(ratingFrom, ratingTo)
        initRatingSliderIndicator(ratingFrom, ratingTo)
    }

    @SuppressLint("SetTextI18n")
    private fun initRatingSliderIndicator(ratingFrom: Float, ratingTo: Float) {
        binding.ratingIndicator.text =
            getString(
                R.string.search_preferences_rating_indicator_text,
                ratingFrom.toString(),
                ratingTo.toString()
            )

    }

    private fun initSortOrderToggleGroup() {
        Log.d("initSortOrderToggleGroup", viewModel.filterOptionsSaved.order)
        when (viewModel.filterOptionsSaved.order) {
            FiltersOrder.RATING -> {
                binding.sortTypesToggleGroup.check(R.id.rating_sort_toggle)
            }

            FiltersOrder.YEAR -> {
                binding.sortTypesToggleGroup.check(R.id.date_sort_toggle)
            }

            FiltersOrder.NUM_VOTE -> {
                binding.sortTypesToggleGroup.check(R.id.popularity_sort_toggle)
            }
        }
    }

    private fun initHaveSeenSwitch() {
        binding.haveSeenSwitch.isChecked = viewModel.filterOptionsSaved.filterNoViewed
    }


    private fun clickListeners() {

        binding.filmTypesToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                viewModel.filterOptionsSaved.type = when (checkedId) {
                    R.id.all_types_toggle ->
                        FiltersTypesOfFilms.ALL

                    R.id.films_types_toggle ->
                        FiltersTypesOfFilms.FILM

                    R.id.serials_types_toggle ->
                        FiltersTypesOfFilms.TV_SERIES

                    else -> {
                        "Unexpected"
                    }
                }
                resetPreferencesButtonActivator()
            }
        }

        binding.countryPreferencesLayout.setOnClickListener {
            val action =
                SearchPreferencesFragmentDirections.actionSearchPreferencesFragmentToCountryPickerFragment()
            findNavController().navigate(action)
        }

        binding.genrePreferencesLayout.setOnClickListener {
            val action =
                SearchPreferencesFragmentDirections.actionSearchPreferencesFragmentToGenrePickerFragment()
            findNavController().navigate(action)
        }

        binding.yearPreferencesLayout.setOnClickListener {
            val action =
                SearchPreferencesFragmentDirections.actionSearchPreferencesFragmentToYearPickerFragment()
            findNavController().navigate(action)
        }

        binding.ratingSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            viewModel.filterOptionsSaved.ratingFrom = values[0]
            viewModel.filterOptionsSaved.ratingTo = values[1]
            initRatingSliderIndicator(values[0], values[1])
            resetPreferencesButtonActivator()
        }

        binding.sortTypesToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                viewModel.filterOptionsSaved.order = when (checkedId) {
                    R.id.rating_sort_toggle -> {
                        FiltersOrder.RATING
                    }


                    R.id.date_sort_toggle -> {
                        FiltersOrder.YEAR
                    }


                    R.id.popularity_sort_toggle -> {
                        FiltersOrder.NUM_VOTE
                    }


                    else -> {
                        "Unexpected"
                    }
                }
            }
            resetPreferencesButtonActivator()
        }

        binding.haveSeenSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.filterOptionsSaved.filterNoViewed = isChecked
            resetPreferencesButtonActivator()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.resetPreferencesButton.setOnClickListener {
            viewModel.resetFilters()
            initView()
        }

        binding.usePreferencesButton.setOnClickListener {
            viewModel.setSavedOptionsInUse()
            findNavController().navigateUp()
        }
    }

    private fun resetPreferencesButtonActivator() {
        Log.d(
            "SearchFragment",
            "settings default ${viewModel.isSearchPreferencesSettingsDefault()}"
        )

        binding.resetPreferencesButton.isEnabled = !viewModel.isSearchPreferencesSettingsDefault()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}