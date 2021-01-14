package com.example.tesis.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tesis.R
import com.example.tesis.databinding.ActivityMainBinding
import com.example.tesis.utils.PagerAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setComponents()
    }

    private fun setComponents() {
        binding.run {
            viewPager.also {
                it.adapter = PagerAdapter(supportFragmentManager, getPagerFragments())
                it.offscreenPageLimit = NUM_PAGES
            }
        }
    }

    private fun getPagerFragments(): List<Fragment> = mutableListOf<Fragment>().apply {
        add(OnboardingFragment.newInstance(getString(R.string.app_description), R.mipmap.ic_onboarding_1))
        add(OnboardingFragment.newInstance(getString(R.string.app_description), R.mipmap.ic_onboarding_2, true))
    }

    companion object {
        private const val NUM_PAGES = 2
    }
}