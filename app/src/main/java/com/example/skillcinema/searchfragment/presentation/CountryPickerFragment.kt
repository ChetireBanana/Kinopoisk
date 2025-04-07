package com.example.skillcinema.searchfragment.presentation

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.R
import com.example.skillcinema.data.models.Country
import com.example.skillcinema.databinding.FragmentSearchPreferencesCountryGenrePickerBinding
import com.example.skillcinema.searchfragment.presentation.GenrePickerFragment.Companion.CHOSEN_ITEM
import com.example.skillcinema.searchfragment.presentation.GenrePickerFragment.Companion.POSITION
import com.example.skillcinema.searchfragment.presentation.adapters.CountryPickerAdapter
import com.example.skillcinema.application.presentation.showBottomSheetError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CountryPickerFragment : Fragment() {

    private var _binding: FragmentSearchPreferencesCountryGenrePickerBinding? = null
    val binding get() = _binding!!

    @Inject
    lateinit var searchFragmentViewModelFactory: SearchFragmentViewModelFactory
    private val viewModel: SearchFragmentViewModel by activityViewModels { searchFragmentViewModelFactory }

    private val adapter = CountryPickerAdapter { item -> onItemClicked(item) }

    private var chosenItem: Country? = null

    private var savedPosition: Int? = null
    private var possibleCountries: List<Country> = listOf()
    private var filteredCountries: List<Country> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentSearchPreferencesCountryGenrePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            chosenItem = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) // API 33
                @Suppress("DEPRECATION")
                savedInstanceState.getParcelable(CHOSEN_ITEM)
            else
                savedInstanceState.getParcelable(CHOSEN_ITEM, Country::class.java)

            savedPosition = savedInstanceState.getInt(POSITION)
        }

        getCountriesList()
        stateObserver()
        possibleCountriesObserver()
        errorObserver()
        clearChoiceButtonActivator()
        listeners()


        binding.title.text = requireContext().getString(R.string.search_preferences_country_title)
        binding.searchFieldEditText.hint =
            requireContext().getString(R.string.country_picker_search_hint)
    }

    private fun getCountriesList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getGenresAndCountriesLists()
        }
    }

    private fun stateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stateFlow.collectLatest { state ->
                when (state) {
                    PageState.PageLoading -> {
                        binding.loaderErrorView.visibility = View.VISIBLE
                        binding.mainScreen.visibility = View.GONE
                    }

                    PageState.PageReady -> {
                        binding.loaderErrorView.visibility = View.GONE
                        binding.mainScreen.visibility = View.VISIBLE
                    }

                    PageState.PageError -> {
                        binding.loaderErrorView.visibility = View.VISIBLE
                        binding.mainScreen.visibility = View.GONE
                        binding.loaderErrorView.stateError()
                        binding.loaderErrorView.setOnClickListener {
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    private fun possibleCountriesObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.countriesFlow.collectLatest { possibleCountriesList ->
                if (possibleCountriesList.isNotEmpty()) {
                    possibleCountries = possibleCountriesList
                    initSearchResultList()
                }
            }
        }
    }

    private fun errorObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.errorListFlow.collect { errorsList ->
                    if (errorsList.isNotEmpty()) {
                        showBottomSheetError(errorsList)
                    }

                }
            }
    }


    private fun listeners() {
        binding.searchFieldEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                showSearchResults(s.toString())
            }
        })

        binding.chosenItemLayout.setOnClickListener {
            choseItem(null)
        }

        binding.clearChoiceButton.setOnClickListener {
            choseItem(null)
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.acceptButton.setOnClickListener {
            viewModel.filterOptionsSaved.countries = chosenItem
            findNavController().navigateUp()
        }
    }

    private fun initSearchResultList() {
        binding.recyclerView.adapter = adapter

        showSearchResults(null)

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(), DividerItemDecoration.VERTICAL
            )
        )

        savedPosition?.let { binding.recyclerView.scrollToPosition(it) }

        val item = chosenItem.takeIf { it != null } ?: viewModel.filterOptionsSaved.countries
        choseItem(item)
    }

    fun showSearchResults(query: String?) {
        filteredCountries = if (query != null) {
            possibleCountries.filter { it.country.contains(query, true) }
        } else {
            possibleCountries
        }

        if (filteredCountries.isNotEmpty()) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.searchFailedPlaceholder.visibility = View.GONE
            adapter.submitList(filteredCountries)
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.searchFailedPlaceholder.visibility = View.VISIBLE
        }

    }

    private fun onItemClicked(itemClicked: Country) {
        val item = itemClicked.takeIf { it != chosenItem }
        choseItem(item)
        clearChoiceButtonActivator()
    }

    private fun choseItem(item: Country?) {
        binding.chosenItemLayout.visibility = if (item != null) View.VISIBLE else View.GONE

        item?.let { binding.chosenItemTextView.text = it.country }
        adapter.choseCountry(item, filteredCountries.indexOf(item))
        chosenItem?.let { adapter.notifyItemChanged(filteredCountries.indexOf(chosenItem)) }
        chosenItem = item
    }


    private fun clearChoiceButtonActivator() {
        binding.clearChoiceButton.isEnabled = chosenItem != null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val layout = binding.recyclerView.layoutManager as LinearLayoutManager

        super.onSaveInstanceState(outState)
        outState.putParcelable(CHOSEN_ITEM, chosenItem)
        outState.putInt(POSITION, layout.findFirstCompletelyVisibleItemPosition())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}