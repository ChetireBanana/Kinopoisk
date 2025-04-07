package com.example.skillcinema.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment(){



    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private val screenList = listOf(
        OnboardingItem(
            message = "Узнавай о премьерах",
            image = R.raw.onboarding1
        ),
        OnboardingItem(
            message = "Создавай коллекции",
            image = R.raw.onboarding2
        ),
        OnboardingItem(
            message = "Делись с друзьями",
            image = R.raw.onboarding3
        ),
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = OnboardingAdapter(screenList)
        binding.dotsIndicator.attachTo(binding.viewPager)

        binding.skipOnboardingButton.setOnClickListener{
            findNavController().navigate(R.id.action_onboardingFragment_to_homepageFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}