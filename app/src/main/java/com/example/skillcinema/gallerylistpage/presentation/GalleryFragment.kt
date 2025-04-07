package com.example.skillcinema.gallerylistpage.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.example.skillcinema.databinding.FragmentGalleryListPageBinding
import com.example.skillcinema.gallerylistpage.presentation.adapters.GalleryImagesPagingAdapter
import com.example.skillcinema.gallerylistpage.presentation.adapters.GalleryViewPagerScreensAdapter
import com.example.skillcinema.gallerylistpage.presentation.adapters.GalleryViewPagerScreensAdapter.GalleryClickListenerInterface
import com.example.skillcinema.application.presentation.imagesTypesTranslator
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GalleryFragment : Fragment(), GalleryViewPagerScreensAdapter.PagingInterface,
    GalleryClickListenerInterface {

    private var _binding: FragmentGalleryListPageBinding? = null
    val binding get() = _binding!!

    private lateinit var bottomSheetsDialog: BottomSheetDialog
    private var bottomSheetsErrorAdapter = BottomSheetsErrorAdapter()
    private var isDialogActive = false

    private val recycledViewPool = RecyclerView.RecycledViewPool()

    @Inject
    lateinit var galleryViewModelFactory: GalleryViewModelFactory
    private val viewModel: GalleryViewModel by viewModels { galleryViewModelFactory }

    private val galleryArgs: GalleryFragmentArgs by navArgs()

    private val galleryAdapter = GalleryViewPagerScreensAdapter(this, this, recycledViewPool)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryListPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("GalleryFragment onViewCreated", "${galleryArgs.kinipoiskId}")
        viewModel.getTabList(galleryArgs.kinipoiskId)
        galleryPageStateObserver()
        galleryTabsObserver()
        galleryErrorObserver()

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }



    private fun galleryPageStateObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.galleryPageState
                    .collect{ state ->
                        when(state){
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

    private fun galleryTabsObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.tabListFlow.collectLatest{ tabsList ->
                    Log.d("GalleryFragment galleryTabsObserver", "$tabsList")
                    if(tabsList.isNotEmpty()) {
                        initAdapterAndTabs(tabsList)
                    }
                }
            }
    }

    private fun galleryErrorObserver() {
        lifecycleScope.launch {
            viewModel.galleryErrorFlow.collect { errorList ->
                if (errorList.isNotEmpty()) {
                    showBottomSheetError(errorList)
                }
            }

        }
    }

    private fun initAdapterAndTabs(tabsList: List<String>) {
        binding.viewPager.adapter = galleryAdapter
        galleryAdapter.submitList(tabsList)

        TabLayoutMediator(binding.filmographyTabLayout, binding.viewPager) { tab, position ->
            val tabName = imagesTypesTranslator(
                requireContext(),
                tabsList[position]
            )
            tab.text = tabName
        }.attach()
    }

    @SuppressLint("InflateParams")
    private fun showBottomSheetError(errorsList: List<String>) {
        if (!isDialogActive) {
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
            bottomSheetsDialog.setOnCancelListener {
                isDialogActive = false
            }
        }
        bottomSheetsErrorAdapter.submitList(errorsList)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPaging(adapter: GalleryImagesPagingAdapter, imagesType: String) {
        viewModel.getImages(galleryArgs.kinipoiskId,imagesType).onEach {
            Log.d("GalleryFragment onPaging", "$it")
            adapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onItemClick(imagePosition: Int, imageType: String) {
        val action = GalleryFragmentDirections.actionGalleryFragmentToFullScreenPagingGalleryDialogFragment(
            galleryArgs.kinipoiskId, imagePosition, imageType
        )
        findNavController().navigate(action)
    }

}