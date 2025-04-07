package com.example.skillcinema.stafflistpage.presentation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.application.presentation.Divider
import com.example.skillcinema.data.models.StaffDto
import com.example.skillcinema.databinding.FragmentStaffListPageBinding
import com.example.skillcinema.filmpage.presentation.adapters.FilmCrewListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StaffListPageFragment : Fragment() {

    private var _binding: FragmentStaffListPageBinding? = null
    private val binding get() = _binding!!

    private val args: StaffListPageFragmentArgs by navArgs()

    private val staffCollectionAdapter = FilmCrewListAdapter { item -> onItemClick(item) }
    private var firstVisibleItemPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStaffListPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstVisibleItemPosition = savedInstanceState?.getInt("position") ?: 0

        initStaffCollectionAdapterAndSetData()
        initClickListeners()
        initSavePosition()


    }

    private fun initSavePosition() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
                firstVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            }
        })
    }

    private fun initClickListeners() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun initStaffCollectionAdapterAndSetData() {
        binding.title.text = args.staffList.title
        binding.recyclerView.adapter = staffCollectionAdapter
        staffCollectionAdapter.setDate(args.staffList.items)
        staffCollectionAdapter.adoptToListPage()
        val itemDecorator = Divider(
            bottomSpacing = requireContext().resources.getDimension(R.dimen.middle_gap)
                .toInt()
        )
        binding.recyclerView.addItemDecoration(itemDecorator)
        binding.recyclerView.scrollToPosition(firstVisibleItemPosition)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("position", firstVisibleItemPosition)
    }


    private fun onItemClick(staffInfoDto: StaffDto) {
        val action =
            StaffListPageFragmentDirections.actionStaffListPageFragmentToPersonalPageFragment(
                staffInfoDto.staffId
            )
        findNavController().navigate(action)
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}