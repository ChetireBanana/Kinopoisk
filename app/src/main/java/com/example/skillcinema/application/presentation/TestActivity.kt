package com.example.skillcinema.application.presentation


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skillcinema.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TestActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

    }
}