package com.example.skillcinema.seasonspage.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.R
import com.example.skillcinema.bottomsheeterror.BottomSheetsErrorAdapter
import com.example.skillcinema.databinding.FragmentSeasonsPageBinding
import com.example.skillcinema.seasonspage.data.SeasonsList
import com.example.skillcinema.seasonspage.presentation.adapters.SeasonPageViewPagerScreenAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SeasonsPageFragment : Fragment() {

    private var _binding: FragmentSeasonsPageBinding? = null
    val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: SeasonsPageViewModelFactory
    private val viewModel: SeasonsPageViewModel by viewModels { viewModelFactory }

    private val args: SeasonsPageFragmentArgs by navArgs()
    private val seasonsAdapter = SeasonPageViewPagerScreenAdapter()

    private lateinit var bottomSheetsDialog: BottomSheetDialog
    private var bottomSheetsErrorAdapter = BottomSheetsErrorAdapter()
    private var isDialogActive = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeasonsPageBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateObserver()
        seasonsListObserver()
        errorObserver()
        loadSeasonsInformation()

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

    }


    private fun stateObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.pageState
                    .collect { state ->
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

    private fun seasonsListObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.seasonsListFlow.collect { seasons ->
                    if (seasons != null) {
                        initAdapterAndTabs(seasons)
                    }
                }
            }
    }


    private fun errorObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.errorFlow.collect { errorList ->
                    if (errorList.isNotEmpty()) {
                        showBottomSheetError(errorList)
                    }
                }
            }
    }

    private fun loadSeasonsInformation() {
        viewModel.collectSeasonsList(args.kinopoiskId)
    }


    private fun initAdapterAndTabs(seasons: SeasonsList) {
        binding.viewPager.adapter = seasonsAdapter
        seasonsAdapter.submitList(seasons.items)

        TabLayoutMediator(binding.fragmentSeasonsTabLayout, binding.viewPager) { tab, position ->
            tab.text = seasons.items[position].number.toString()
        }.attach()
    }

    @SuppressLint("InflateParams")
    private fun showBottomSheetError(errorsList: List<String>) {
        if (!isDialogActive) {
            isDialogActive = true
            val bottomSheetDialogView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            bottomSheetsDialog = BottomSheetDialog(requireContext())
            bottomSheetsDialog.setContentView(bottomSheetDialogView)
            val bottomSheetRecyclerView =
                bottomSheetDialogView.findViewById<RecyclerView>(R.id.bottom_sheets_dialog_message)
            val bottomSheetDialogCloseButton =
                bottomSheetDialogView.findViewById<MaterialButton>(R.id.close_button)
            bottomSheetRecyclerView.adapter = bottomSheetsErrorAdapter
            bottomSheetDialogCloseButton.setOnClickListener {
                bottomSheetsDialog.dismiss()
            }
            bottomSheetsDialog.show()
            bottomSheetsDialog.dismissWithAnimation = true
        }
        bottomSheetsDialog.setOnCancelListener {
            isDialogActive = false
        }
        bottomSheetsErrorAdapter.submitList(errorsList)
    }
}