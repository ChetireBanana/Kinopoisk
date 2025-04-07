package com.example.skillcinema.personalpage.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.application.presentation.Divider
import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.bottomsheeterror.BottomSheetsErrorAdapter
import com.example.skillcinema.data.models.PersonInfoFilmDto
import com.example.skillcinema.databinding.FragmentPersonalPageBinding
import com.example.skillcinema.personalpage.data.PersonInfoDto
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


class PersonalPageFragmentCopy : Fragment(),
    PersonalPageFilmographyAdapter.PersonalPageFilmographyAdapterInterface {


    private var _binding: FragmentPersonalPageBinding? = null
    private val binding get() = _binding!!

    private val args: PersonalPageFragmentArgs by navArgs()

    private var errorCaught = false

    private lateinit var bottomSheetsDialog: BottomSheetDialog
    private var bottomSheetsErrorAdapter = BottomSheetsErrorAdapter()

    private var isFilmographyLoaded = false

    private var personInfo: PersonInfoDto? = null

    @Inject
    lateinit var viewModelFactory: PersonalPageViewModelFactory
    private val viewModel: PersonalPageViewModel by activityViewModels { viewModelFactory }

    private val filmAdapter =
        PersonalPageFilmographyAdapter(this)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        stateObserver()
        personalInfoObserver()
        personalInfoFilmsObserver()
        personalInfoErrorObserver()

        loadPersonalInfo()
        initAdapter()
        clickListeners()

    }

    private fun stateObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.personalPageStateFlow
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

                            }
                        }
                    }
            }
    }


    private fun personalInfoObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.personInfoFlow.collectLatest { info ->
                if (info != null) {
                    personInfo = info
                    initPersonalInfoPage(info)
                }
            }
        }
    }

    private fun personalInfoFilmsObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.personInfoFilmsFlow.collectLatest { films ->
                if (films != null) {
                    isFilmographyLoaded = true
                    initFilmography(films)
                }
            }
        }
    }

    private fun personalInfoErrorObserver(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.personInfoErrorFlow.collectLatest { error ->
                if (error != null) {
                    errorCaught = true
                    showBottomSheetError(error)
                }
            }
        }
    }

    private fun loadPersonalInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getPersonInfo(args.staffId)
        }
    }

    private fun initPersonalInfoPage(info: PersonInfoDto) {
        val name = info.nameRu ?: (info.nameEn ?: getString(R.string.no_name))
        binding.name.text = name

        if (info.profession != null)
            binding.profession.text = info.profession
        Log.d("initPersonalInfoPage", info.posterUrl.toString())

        Glide
            .with(requireContext())
            .load(info.posterUrl)
            .into(binding.photo)


        if (!info.films.isNullOrEmpty()) {
            if (!isFilmographyLoaded) {
                initFilmography(info.films!!)
            }

            val filmsQuantity = resources.getQuantityString(
                R.plurals.filmography_description,
                info.films!!.size,
                info.films!!.size
            )
            binding.filmographyDescription.text = filmsQuantity

        } else {
            binding.bestTitle.visibility = View.GONE
            binding.bestWorksRecycler.visibility = View.GONE
            binding.showAllFilmographyButton.visibility = View.GONE
        }
    }


    private fun initAdapter() {
        binding.bestWorksRecycler.adapter = filmAdapter
        val dividerItemDecoration = Divider(
            rightSpacing = 8
        )
        binding.bestWorksRecycler.addItemDecoration(dividerItemDecoration)
    }


    private fun clickListeners() {
        binding.showAllFilmographyButton.setOnClickListener {
            val action =
                PersonalPageFragmentDirections.actionPersonalPageFragmentToFilmographyListPageFragment(
                    personInfo!!.personId
                )

            findNavController().navigate(action)
        }
        binding.photo.setOnClickListener {
            val action =
                PersonalPageFragmentDirections.actionPersonalPageFragmentToFullScreenImageDialogFragment(
                    personInfo!!.posterUrl!!
                )
            findNavController().navigate(action)
        }


        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun initFilmography(films: List<PersonInfoFilmDto>) {
        filmAdapter.setData(films)
    }

    override fun onItemClick(item: PersonInfoFilmDto) {
        val action =
            PersonalPageFragmentDirections.actionPersonalPageFragmentToFilmPageFragment(item.filmId)
        findNavController().navigate(action)
    }

    override fun checkIfViewed(itemId: Int, callback: (Boolean) -> Unit) {
        viewModel.checkIfViewed(itemId, callback)
    }

    @SuppressLint("InflateParams")
    private fun showBottomSheetError(errorsList: List<String>) {
        if (!errorCaught){
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
        }
        bottomSheetsErrorAdapter.submitList(errorsList)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}