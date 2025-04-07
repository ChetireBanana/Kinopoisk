package com.example.skillcinema.createcollectiondialogfragment.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.skillcinema.R
import com.example.skillcinema.databinding.DialogFragmentCreateCollectionBinding
import com.example.skillcinema.localdatabase.entities.NewLocalCollectionEntity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateCollectionDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentCreateCollectionBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var createCollectionViewModelFactory: CreateCollectionViewModelFactory
    private val createCollectionViewModel: CreateCollectionViewModel by viewModels { createCollectionViewModelFactory }

    private var collectionTitle: String? = null
    private var collectionIcon: Int = R.drawable.icon_profile

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentCreateCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        listeners()
    }

    private fun listeners() {
        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.createCollectionReadyButton.setOnClickListener {
            Log.d("CreateCollectionDialogFragment", "init")
            if (collectionTitle != null) {
                createCollectionViewModel.insertCollectionToDB(
                    NewLocalCollectionEntity(
                        title = collectionTitle!!,
                        icon = collectionIcon,
                        size = 0
                    )
                )
            }
            dismiss()
        }


        val handler = Handler(Looper.getMainLooper())

        binding.collectionTitleEditTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val title = p0.toString()

                if (title.trim().isEmpty()) {
                    binding.createCollectionReadyButton.isEnabled = false
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        binding.collectionTitleEditTextLayout.error =
                            "Введите название коллекции чтобы её создать"
                    }, 3000)
                } else {
                    binding.collectionTitleEditTextLayout.error = null
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        createCollectionViewModel.checkIfLocalCollectionExistByTitle(title) { exist ->
                            if (exist) {
                                Log.d("CreateCollectionDialogFragment", "exist")
                                binding.collectionTitleEditTextLayout.error =
                                    "Коллекция с таким названием уже существует"
                            } else {
                                Log.d("CreateCollectionDialogFragment", "not exist")
                                collectionTitle = title
                                binding.createCollectionReadyButton.isEnabled = true
                            }
                        }
                    }, 1000)
                }
            }
        })

        binding.choseIconToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.choseBullhornIconButton.id -> {
                        collectionIcon = R.drawable.icon_bullhorn
                    }

                    binding.choseFireIconButton.id -> {
                        collectionIcon = R.drawable.icon_fire
                    }

                    binding.choseHeartIconButton.id -> {
                        collectionIcon = R.drawable.icon_heart
                    }

                    binding.choseUniversityIconButton.id -> {
                        collectionIcon = R.drawable.icon_university
                    }

                    binding.choseLightbulbIconButton.id -> {
                        collectionIcon = R.drawable.icon_lightbulb
                    }

                    binding.choseBookmarkIconButton.id -> {
                        collectionIcon = R.drawable.icon_bookmark
                    }

                    else -> {
                        collectionIcon = R.drawable.icon_profile
                    }
                }
            }
        }
    }
}