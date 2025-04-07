package com.example.skillcinema.application.presentation

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.bottomsheeterror.BottomSheetsErrorAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton


@SuppressLint("StaticFieldLeak")
private var currentBottomSheetErrorDialog: BottomSheetDialog? = null

@SuppressLint("InflateParams")
fun Fragment.showBottomSheetError(errorsList: List<String>) {

    if (currentBottomSheetErrorDialog != null) {
        val recyclerView =
            currentBottomSheetErrorDialog!!.findViewById<RecyclerView>(R.id.bottom_sheets_dialog_message)

        val adapter = BottomSheetsErrorAdapter()
        recyclerView?.adapter = adapter
        adapter.submitList(errorsList)

    } else {
        val bottomSheetDialogView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        currentBottomSheetErrorDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(bottomSheetDialogView)
            findViewById<RecyclerView>(R.id.bottom_sheets_dialog_message)?.apply {
                adapter = BottomSheetsErrorAdapter().also { adapter ->
                    adapter.submitList(errorsList)
                }
            }
            findViewById<MaterialButton>(R.id.close_button)?.setOnClickListener {
                dismiss()
                currentBottomSheetErrorDialog = null
            }
            show()
        }
    }
}
