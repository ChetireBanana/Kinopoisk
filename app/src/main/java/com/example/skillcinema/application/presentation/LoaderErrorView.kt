package com.example.skillcinema.application.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.google.android.material.progressindicator.CircularProgressIndicator

class LoaderErrorView(
    context: Context,
    attrs: AttributeSet,
): LinearLayout(context, attrs){

    private var listener: LoaderErrorViewButtonsInterface? = null

    init {
        inflate(context, R.layout.view_loader, this)

        Glide
            .with(context)
            .load(R.raw.onboarding1)
            .into(findViewById(R.id.loader_image))

        listeners()
    }

    fun setListener(listener: LoaderErrorViewButtonsInterface) {
        this.listener = listener
    }

    interface LoaderErrorViewButtonsInterface{
        fun onRefreshButtonClick()
        fun onBackButtonClick()
    }


    fun listeners(){
        val refreshButton = findViewById<Button>(R.id.loader_view_refresh_button)
        refreshButton.setOnClickListener {
            listener?.onRefreshButtonClick()
        }

        val backButton = findViewById<Button>(R.id.loader_view_back_button)
        backButton.setOnClickListener {
            listener?.onBackButtonClick()
        }
    }


    fun stateError(){
        findViewById<CircularProgressIndicator>(R.id.progress_circular).visibility = View.GONE
        findViewById<LinearLayout>(R.id.error_layout).visibility = View.VISIBLE
    }
}