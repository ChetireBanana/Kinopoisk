package com.example.skillcinema.fullscreenImagedialog.presentation

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.databinding.DialogFullScreenImageBinding


class FullScreenImageDialogFragment : DialogFragment() {

    private var _binding: DialogFullScreenImageBinding? = null
    private val binding get() = _binding!!

    private val args: FullScreenImageDialogFragmentArgs by navArgs()


    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogFullScreenImageBinding.inflate(LayoutInflater.from(context))

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setGravity(Gravity.CENTER)



        binding.dialogFullScreenImageView.let {
            Glide
                .with(requireContext())
                .load(args.imageLink)
                .into(it)
        }



        binding.closeButton.setOnClickListener {
            dialog?.dismiss()
        }

        return AlertDialog.Builder(requireActivity(), R.style.GalleryDialogStyle)
            .setView(binding.root)
            .create()

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}