package com.example.skillcinema.filmpage.presentation

import android.annotation.SuppressLint
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.application.presentation.Divider
import com.example.skillcinema.application.presentation.PageState
import com.example.skillcinema.application.presentation.showBottomSheetError
import com.example.skillcinema.data.models.FilmCollection
import com.example.skillcinema.data.models.FilmDto
import com.example.skillcinema.data.models.StaffDto
import com.example.skillcinema.data.models.StaffList
import com.example.skillcinema.databinding.FragmentFilmpageBinding
import com.example.skillcinema.filmpage.presentation.adapters.FilmCrewListAdapter
import com.example.skillcinema.filmpage.presentation.adapters.FilmPageGalleryAdapter
import com.example.skillcinema.gallerylistpage.data.ImagesCollection
import com.example.skillcinema.homepage.presentation.adapters.MovieCollectionAdapter
import com.example.skillcinema.localdatabase.data.LocalBaseCollections
import com.example.skillcinema.localdatabase.entities.CollectionItemEntity
import com.example.skillcinema.seasonspage.data.SeasonsList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FilmPageFragment : Fragment(), MovieCollectionAdapter.MovieCollectionAdapterInterface {


    private var _binding: FragmentFilmpageBinding? = null
    private val binding get() = _binding!!

    private val args: FilmPageFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: FilmPageViewModelFactory
    private val viewModel: FilmPageViewModel by viewModels { viewModelFactory }

    private var seasonsList: SeasonsList? = null
    private var actors: StaffList? = null
    private var staff: StaffList? = null
    private var images: ImagesCollection? = null
    private var similars: FilmCollection? = null
    private var collectionsForFilmId: List<CollectionItemEntity> = listOf()


    private var actorsListAdapter = FilmCrewListAdapter { crewItem -> onStaffItemClicked(crewItem) }
    private var crewListAdapter = FilmCrewListAdapter { crewItem -> onStaffItemClicked(crewItem) }
    private var filmPageGalleryAdapter =
        FilmPageGalleryAdapter { position -> onGalleryItemClicked(position) }
    private var similarMoviesCollectionAdapter =
        MovieCollectionAdapter(this)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmpageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFilmInfo(args.kinopoiskId)
        initFilmPage()
        stateObserver()
        clickListeners()

    }

    private fun clickListeners() {
        binding.longDescription.setOnClickListener {
            binding.longDescription.toggle()
        }
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.showAllSeasonsButton.setOnClickListener {
            val action =
                FilmPageFragmentDirections.actionFilmPageFragmentToSeasonsPageFragment(args.kinopoiskId)
            findNavController().navigate(action)
        }
        binding.showAllActorsButton.setOnClickListener {
            actors?.let {
                val action =
                    FilmPageFragmentDirections.actionFilmPageFragmentToStaffListPageFragment(it)
                findNavController().navigate(action)
            }
        }
        binding.showAllCreatorsButton.setOnClickListener {
            staff?.let {
                val action =
                    FilmPageFragmentDirections.actionFilmPageFragmentToStaffListPageFragment(it)
                findNavController().navigate(action)
            }
        }
        binding.showAllGalleryButton.setOnClickListener {
            val action =
                FilmPageFragmentDirections.actionFilmPageFragmentToGalleryFragment(args.kinopoiskId)
            findNavController().navigate(action)
        }

        binding.showAllSimilarMoviesButton.setOnClickListener {
            similars?.let {
                val action =
                    FilmPageFragmentDirections
                        .actionFilmPageFragmentToFilmCollectionListPageFragment(it)
                findNavController().navigate(action)
            }
        }

        binding.buttonPanelToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                binding.addToBookmarkCollectionButton.id -> {
                    buttonsPanelToggleButtonsListener(LocalBaseCollections.BOOKMARK_ID, isChecked)
                }

                binding.addToViewedCollectionButton.id -> {
                    buttonsPanelToggleButtonsListener(LocalBaseCollections.VIEWED_ID, isChecked)
                }

                binding.addToFavouriteCollectionButton.id -> {
                    buttonsPanelToggleButtonsListener(LocalBaseCollections.FAVOURITE_ID, isChecked)
                }
            }
        }

        binding.shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "${viewModel.webUrl}}")
            }
            val chooser = Intent.createChooser(shareIntent, getString(R.string.share_dialog_title))
            startActivity(chooser)
        }


        binding.addToOtherCollectionButton.setOnClickListener {
            val action =
                FilmPageFragmentDirections.actionFilmPageFragmentToBottomSheetAddToCollectionDialogFragment(
                    args.kinopoiskId
                )

            findNavController().navigate(action)
        }
    }

    private fun buttonsPanelToggleButtonsListener(collectionId: Int, isChecked: Boolean) {
        if (isChecked) {
            viewModel.insertItemInLocalCollectionAndResetSize(collectionId, args.kinopoiskId)
        } else {
            viewModel.deleteItemFromLocalCollectionAndResetSize(collectionId, args.kinopoiskId)
        }
    }

    private fun loadFilmInfo(kinopoiskId: Int) {
        Log.d("loadFilmInfo", kinopoiskId.toString())
        lifecycleScope.launch {
            viewModel.loadFilmInfo(kinopoiskId)
        }
    }

    private fun initFilmPage() {
        filmMainDescriptionObserver()
        seasonsAndSeriesObserver()
        actorsObserver()
        staffObserver()
        galleryObserver()
        similarMoviesObserver()
        initButtonPanelButtons()
        errorsObserver()
        collectionsForFilmObserver()
    }


    private fun filmMainDescriptionObserver() {
        Log.d("filmInfoObserver", "init filmInfoObserver")
        lifecycleScope.launch {
            viewModel.mainDescriptionFlow.collect { filmCollected ->
                if (filmCollected != null) {
                    Log.d("filmInfoObserver", "film collected ${filmCollected.ratingAgeLimits}")
                    initMainDescriptionBlock(filmCollected)
                }
            }

        }
    }

    private fun seasonsAndSeriesObserver() {
        lifecycleScope.launch {
            viewModel.seasonsInfoFlow.collect { seasonsInfo ->
                if (seasonsInfo != null) {
                    Log.d("seasonsAndSeriesObserver", " ${seasonsInfo.total}")
                    seasonsList = seasonsInfo
                    initSeasonsBlock(seasonsInfo)
                }
            }
        }
    }

    private fun actorsObserver() {
        lifecycleScope.launch {
            viewModel.actorsFlow.collect { actorsInfo ->
                if (actorsInfo != null) {
                    actors = actorsInfo
                    initActorsBlock(actorsInfo)
                }
            }
        }
    }

    private fun staffObserver() {
        lifecycleScope.launch {
            viewModel.staffFlow.collect { staffInfo ->
                if (staffInfo != null) {
                    staff = staffInfo
                    initCreatorsBlock(staffInfo)
                }
            }
        }
    }

    private fun galleryObserver() {
        lifecycleScope.launch {
            viewModel.imagesFlow.collect { imagesCollected ->
                if (imagesCollected != null) {
                    images = imagesCollected
                    initGalleryBlock(imagesCollected)
                }
            }
        }
    }

    private fun errorsObserver() {
        lifecycleScope.launch {
            viewModel.errorsFlow.collect { errorList ->
                if (errorList.isNotEmpty()) {
                    showBottomSheetError(errorList)
                }
            }

        }
    }


    private fun similarMoviesObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.similarFilmsFlow.collect { similarFilms ->
                    if (similarFilms != null) {
                        similars = similarFilms
                        initSimilarMoviesBlock(similarFilms)
                    }
                }
            }
    }


    private fun stateObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.filmPgeState.collect { state ->
                    when (state) {
                        PageState.PageLoading -> {
                            binding.loaderErrorView.visibility = View.VISIBLE
                            binding.mainScreen.visibility = View.GONE
                        }

                        PageState.PageReady -> {
                            binding.loaderErrorView.visibility = View.GONE
                            binding.mainScreen.visibility = View.VISIBLE
                        }

                        is PageState.PageError -> {
                            binding.loaderErrorView.visibility = View.VISIBLE
                            binding.mainScreen.visibility = View.GONE
                            binding.loaderErrorView.stateError()
                        }
                    }
                }
            }
    }

    private fun collectionsForFilmObserver() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                viewModel.getCollectionsFlowForItemId(args.kinopoiskId).collect { collections ->
                    collectionsForFilmId = collections
                    initButtonPanelButtons()
                }
            }
    }


    private fun initMainDescriptionBlock(filmWithInfo: FilmDto) {
        Glide
            .with(requireContext())
            .load(filmWithInfo.posterUrl)
            .into(binding.filmPoster)
        if (filmWithInfo.logoUrl != null) {
            binding.filmLogo.visibility = View.VISIBLE
            binding.filmTitle.visibility = View.GONE
            Glide
                .with(requireContext())
                .load(filmWithInfo.logoUrl)
                .into(binding.filmLogo)
        } else {
            binding.filmLogo.visibility = View.GONE
            binding.filmTitle.visibility = View.VISIBLE
            binding.filmTitle.text = filmWithInfo.nameRu ?: filmWithInfo.nameOriginal
        }

        val rating = if (filmWithInfo.ratingKinopoisk != null) {
            filmWithInfo.ratingKinopoisk.toString()
        } else {
            if (filmWithInfo.ratingImdb != null) {
                filmWithInfo.ratingImdb.toString()
            } else {
                null
            }
        }

        val ratingOtherName: List<String?> =
            listOfNotNull(rating, filmWithInfo.nameOriginal)
        binding.ratingOtherName.text = ratingOtherName.joinToString(", ")

        val genre = if (filmWithInfo.genres.isNotEmpty()) {
            filmWithInfo.genres[0].genre
        } else {
            null
        }

        val yearGenresSeasons: List<String?> = listOfNotNull(
            filmWithInfo.year.toString(),
            genre
        )
        binding.yearGenresSeasons.text = yearGenresSeasons.joinToString(", ")

        val length = filmWithInfo.filmLength
        val duration = if (length != null) {
            val lengthHours = length / 60
            val lengthMinutes = length % 60
            "${lengthHours}ч ${lengthMinutes}мин"
        } else {
            null
        }

        val ratingAgeLimits = filmWithInfo.ratingAgeLimits
        val ratingAge = if (ratingAgeLimits != null) {
            "${ratingAgeLimits.filter { it.isDigit() }}+"
        } else {
            null
        }

        val country = if (filmWithInfo.countries.isNotEmpty()) {
            filmWithInfo.countries[0].country
        } else {
            null
        }

        val countryLengthAge: List<String?> =
            listOfNotNull(country, duration, ratingAge)
        binding.countryLengthAge.text = countryLengthAge.joinToString(", ")

        if (filmWithInfo.shortDescription != null) {
            binding.shortDescription.text = filmWithInfo.shortDescription
        } else {
            binding.shortDescription.visibility = View.GONE
        }

        if (filmWithInfo.description != null) {
            binding.longDescription.text = filmWithInfo.description
        } else {
            binding.longDescription.visibility = View.GONE
        }
    }

    private fun initSeasonsBlock(seasonsListInfo: SeasonsList) {
        val seasonsQuantity = resources.getQuantityString(
            R.plurals.seasons_description,
            seasonsListInfo.total,
            seasonsListInfo.total
        )
        val episodesQuantity = resources.getQuantityString(
            R.plurals.episodes_description,
            seasonsListInfo.total,
            seasonsListInfo.total
        )
        val seasonsDescription = if (seasonsListInfo.total == 0) {
            listOf(seasonsQuantity, episodesQuantity).joinToString(" ")
        } else {
            episodesQuantity
        }
        binding.seasonsAndSeriesDescription.text = seasonsDescription
        binding.yearGenresSeasons.text =
            listOf(binding.yearGenresSeasons.text, seasonsQuantity).joinToString(", ")
        binding.seasonsLayout.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun initActorsBlock(actorsList: StaffList) {
        binding.actorsListRecyclerView.adapter = actorsListAdapter
        val maxSize = 20
        actorsListAdapter.setDate(actorsList.items)
        actorsListAdapter.setMaxSize(maxSize)
        val dividerItemDecoration = Divider(
            bottomSpacing = resources.getDimensionPixelSize(R.dimen.smallest_gap),
            rightSpacing = resources.getDimensionPixelSize(R.dimen.smallest_gap),
            rightFooter = resources.getDimensionPixelSize(R.dimen.start_end_margin),
        )
        binding.actorsListRecyclerView.addItemDecoration(dividerItemDecoration)
        if (actorsList.items.size > maxSize) {
            binding.showAllActorsButton.visibility = View.VISIBLE
            binding.showAllActorsButton.text = actorsList.items.size.toString()
        }

        binding.actorsLayout.visibility = View.VISIBLE

    }

    @SuppressLint("SetTextI18n")
    private fun initCreatorsBlock(creatorsList: StaffList) {
        binding.creatorsListRecyclerView.adapter = crewListAdapter
        val maxSize = 6
        crewListAdapter.setDate(creatorsList.items)
        crewListAdapter.setMaxSize(maxSize)
        val dividerItemDecoration = Divider(
            bottomSpacing = resources.getDimensionPixelSize(R.dimen.smallest_gap),
            rightSpacing = resources.getDimensionPixelSize(R.dimen.smallest_gap),
            rightFooter = resources.getDimensionPixelSize(R.dimen.start_end_margin)
        )
        binding.creatorsListRecyclerView.addItemDecoration(dividerItemDecoration)
        if (creatorsList.items.size > maxSize) {
            binding.showAllCreatorsButton.visibility = View.VISIBLE
            binding.showAllCreatorsButton.text = creatorsList.items.size.toString()
        }
        binding.creatorLayout.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun initGalleryBlock(imagesCollected: ImagesCollection) {
        binding.galleryListRecyclerView.adapter = filmPageGalleryAdapter

        if (imagesCollected.items.size > 20) {
            binding.showAllGalleryButton.visibility = View.VISIBLE
            binding.showAllGalleryButton.text = imagesCollected.items.size.toString()
        }
        val itemDecoration = Divider(
            rightSpacing = resources.getDimensionPixelSize(R.dimen.smallest_gap)
        )
        binding.galleryListRecyclerView.addItemDecoration(itemDecoration)
        filmPageGalleryAdapter.setData(imagesCollected.items)
        binding.gallery.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun initSimilarMoviesBlock(similarFilms: FilmCollection) {
        binding.similarMoviesListRecyclerView.adapter = similarMoviesCollectionAdapter
        Log.d("similarFilms", "initSimilarMoviesBlock ${similarFilms.items.size} ")
        similarMoviesCollectionAdapter.setData(similarFilms.items)
        if (similarFilms.items.size > 20) {
            binding.showAllSimilarMoviesButton.visibility = View.VISIBLE
            binding.showAllSimilarMoviesButton.text = similarFilms.items.size.toString()
        }
        val itemDecoration = Divider(
            rightSpacing = resources.getDimensionPixelSize(R.dimen.smallest_gap)
        )
        binding.similarMoviesListRecyclerView.addItemDecoration(itemDecoration)
        binding.similarMoviesLayout.visibility = View.VISIBLE
    }

    private fun initButtonPanelButtons() {

        if (collectionsForFilmId.any { it.collectionId == LocalBaseCollections.VIEWED_ID }) {
            binding.addToViewedCollectionButton.isChecked = true
        }
        if (collectionsForFilmId.any { it.collectionId == LocalBaseCollections.FAVOURITE_ID }) {
            binding.addToFavouriteCollectionButton.isChecked = true
        }
        if (collectionsForFilmId.any { it.collectionId == LocalBaseCollections.BOOKMARK_ID }) {
            binding.addToBookmarkCollectionButton.isChecked = true
        }
    }


    private fun onStaffItemClicked(crewItem: StaffDto) {
        val action =
            FilmPageFragmentDirections.actionFilmPageFragmentToPersonalPageFragment(crewItem.staffId)
        findNavController().navigate(action)
    }

    private fun onGalleryItemClicked(position: Int) {
        if (images != null) {
            val action =
                FilmPageFragmentDirections.actionFilmPageFragmentToFullScreenGalleryDialogFragment(
                    images!!, position
                )
            findNavController().navigate(action)
        }
    }

    override fun onItemClick(item: FilmDto) {
        val action = FilmPageFragmentDirections.actionFilmPageFragmentSelf(
            item.kinopoiskId
        )
        findNavController().navigate(action)
    }

    override fun checkIfViewed(id: Int, callback: (Boolean) -> Unit) {
        viewModel.checkIfViewed(id, callback)
    }

}