package com.example.skillcinema.searchfragment.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.skillcinema.R
import com.example.skillcinema.application.presentation.YEAR_PICKER_SPAN_COUNT
import com.example.skillcinema.databinding.FragmentSearchPreferencesYearPickerBinding
import com.example.skillcinema.searchfragment.presentation.adapters.YearPickerViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class YearPickerFragment : Fragment(), YearPickerViewPagerAdapter.YearPickerAdapterInterface {

    private var _binding: FragmentSearchPreferencesYearPickerBinding? = null
    val binding get() = _binding!!


    @Inject
    lateinit var searchFragmentViewModelFactory: SearchFragmentViewModelFactory
    private val viewModel: SearchFragmentViewModel by activityViewModels { searchFragmentViewModelFactory }

    private val yearFromAdapter = YearPickerViewPagerAdapter(YEAR_FROM, this)
    private val yearToAdapter = YearPickerViewPagerAdapter(YEAR_TO, this)

    private val yearRange = (1895..3000).toList()
    private var yearRangeChunked = emptyList<List<Int>>()

    private var yearFrom: Int? = null
    private var yearTo: Int? = null

    private var yearFromPosition: Int? = null
    private var yearToPosition: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchPreferencesYearPickerBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            yearFrom = savedInstanceState.getInt(YEAR_FROM)
            yearTo = savedInstanceState.getInt(YEAR_TO)
            yearFromPosition = savedInstanceState.getInt(YEAR_FROM_POSITION)
            yearToPosition = savedInstanceState.getInt(YEAR_TO_POSITION)
        }

        initAdapters()
        initYearIndicators()
        clickListeners()
        controlButtonsFromYearActivator()
        controlButtonsToYearActivator()
        clearChoiceButtonActivator()
    }

    private fun initAdapters() {
        val viewTreeObserver = binding.yearFromViewPager.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (binding.yearToViewPager.width > 0) {
                    binding.yearFromViewPager.adapter = yearFromAdapter
                    yearFromAdapter.setViewWidth(binding.yearToViewPager.width)
                    binding.yearToViewPager.adapter = yearToAdapter
                    yearToAdapter.setViewWidth(binding.yearToViewPager.width)

                    binding.yearFromViewPager.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val viewWidth = binding.yearToViewPager.width
                    val itemWidth =
                        requireContext().resources.getDimension(R.dimen.year_item_width)
                            .toInt()
                    val columnsCount = (viewWidth / itemWidth)
                    val itemsCount = columnsCount * YEAR_PICKER_SPAN_COUNT

                    yearRangeChunked = yearRange.chunked(itemsCount)

                    yearFromAdapter.setData(yearRangeChunked)
                    yearToAdapter.setData(yearRangeChunked)

                    if (yearFromPosition == null) {
                        binding.yearFromViewPager.currentItem = 0
                    } else {
                        val firstItemFromYear =
                            yearRangeChunked.indexOfFirst { it.contains(yearFromPosition) }
                        binding.yearFromViewPager.currentItem = firstItemFromYear
                    }

                    if (yearToPosition == null) {
                        binding.yearToViewPager.currentItem = yearRangeChunked.size - 1
                    } else {
                        val firstItemToYear =
                            yearRangeChunked.indexOfFirst { it.contains(yearToPosition) }
                        binding.yearToViewPager.currentItem = firstItemToYear
                    }

                    if (yearFrom != null) {
                        yearFromAdapter.choseYear(yearFrom)
                    }

                    if (yearTo != null) {
                        yearToAdapter.choseYear(yearTo)
                    }
                }
            }
        })
    }

    private fun initYearIndicators() {
        if (!isViewReady(binding.yearFromViewPager)) {
            binding.yearFromViewPager.post { initYearIndicators() }
            return
        }

        val currentItemFrom = binding.yearFromViewPager.currentItem
        val neededItemFrom = yearRangeChunked[currentItemFrom]

        binding.yearFromIndicator.text = listOf(
            neededItemFrom.first().toString(),
            neededItemFrom.last().toString()
        ).joinToString(separator = " - ")

        val currentItemTo = binding.yearToViewPager.currentItem
        val neededItemTo = yearRangeChunked[currentItemTo]

        binding.yearToIndicator.text = listOf(
            neededItemTo.first().toString(),
            neededItemTo.last().toString()
        ).joinToString(separator = " - ")
    }

    private fun clickListeners() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.acceptButton.setOnClickListener {
            viewModel.filterOptionsSaved.yearFrom = yearFrom
            viewModel.filterOptionsSaved.yearTo = yearTo
            findNavController().navigateUp()
        }

        binding.clearChoiceButton.setOnClickListener {
            yearFrom = null
            yearTo = null
            yearFromAdapter.choseYear(null)
            yearToAdapter.choseYear(null)
            clearChoiceButtonActivator()
        }

        binding.yearFromBackwardButton.setOnClickListener {
            if (binding.yearFromViewPager.currentItem != 0) {
                binding.yearFromViewPager.currentItem -= 1
            }
        }

        binding.yearFromForwardButton.setOnClickListener {
            if (binding.yearFromViewPager.currentItem != yearRange.size - 1) {
                binding.yearFromViewPager.currentItem += 1
            }
        }

        binding.yearToBackwardButton.setOnClickListener {
            if (binding.yearToViewPager.currentItem != 0) {
                binding.yearToViewPager.currentItem -= 1

            }
        }

        binding.yearToForwardButton.setOnClickListener {
            if (binding.yearToViewPager.currentItem != yearRange.size - 1) {
                binding.yearToViewPager.currentItem += 1
            }
        }

        binding.yearFromViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    controlButtonsFromYearActivator()
                    initYearIndicators()
                }
            })

        binding.yearToViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    controlButtonsToYearActivator()
                    initYearIndicators()
                }
            })


    }


    private fun controlButtonsFromYearActivator() {
        binding.yearFromBackwardButton.isEnabled = binding.yearFromViewPager.currentItem > 0
        binding.yearFromForwardButton.isEnabled =
            binding.yearFromViewPager.currentItem < yearRangeChunked.size - 1
    }

    private fun controlButtonsToYearActivator() {
        binding.yearToBackwardButton.isEnabled = binding.yearToViewPager.currentItem > 0
        binding.yearToForwardButton.isEnabled =
            binding.yearToViewPager.currentItem < yearRangeChunked.size - 1
    }

    private fun clearChoiceButtonActivator() {
        binding.clearChoiceButton.isEnabled = !(yearFrom == null && yearTo == null)
    }


    override fun onItemClicked(pagerId: String, onItemClicked: Int) {
        when (pagerId) {
            YEAR_FROM -> handleClickOnYearFrom(onItemClicked)
            YEAR_TO -> handleClickOnYearTo(onItemClicked)
        }
        clearChoiceButtonActivator()
    }

    private fun handleClickOnYearFrom(onItemClicked: Int) {
        if (onItemClicked == yearFrom) {
            yearFrom = null
            yearFromAdapter.choseYear(null)
        } else {
            yearFrom = onItemClicked
            yearFromAdapter.choseYear(onItemClicked)
        }
    }

    private fun handleClickOnYearTo(onItemClicked: Int) {
        if (onItemClicked == yearTo) {
            yearTo = null
            yearToAdapter.choseYear(null)
        } else {
            yearTo = onItemClicked
            yearToAdapter.choseYear(onItemClicked)
        }
    }

    private fun isViewReady(view: View): Boolean {
        return view.width > 0 && view.height > 0
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        yearFrom?.let { outState.putInt(YEAR_FROM, it) }
        yearTo?.let { outState.putInt(YEAR_TO, it) }

        val currentItemFromYear = binding.yearFromViewPager.currentItem
        val firstVisiblePositionFromYear = yearRangeChunked[currentItemFromYear].first()
        outState.putInt(YEAR_FROM_POSITION, firstVisiblePositionFromYear)

        val currentItemToYear = binding.yearToViewPager.currentItem
        val firstVisiblePositionToYear = yearRangeChunked[currentItemToYear].first()
        outState.putInt(YEAR_TO_POSITION, firstVisiblePositionToYear)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val YEAR_FROM = "yearFrom"
        private const val YEAR_FROM_POSITION = "yearFromPosition"
        private const val YEAR_TO = "yearTo"
        private const val YEAR_TO_POSITION = "yearToPosition"
    }
}