package com.example.skillcinema.application.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.skillcinema.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val navController = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
            ?.findNavController()

        if (navController != null) {
            bottomNavigationView.setupWithNavController(navController)
            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id == R.id.onboardingFragment) {
                    bottomNavigationView.visibility = BottomNavigationView.GONE
                } else {
                    bottomNavigationView.visibility = BottomNavigationView.VISIBLE
                }
            }
        }

    }
}