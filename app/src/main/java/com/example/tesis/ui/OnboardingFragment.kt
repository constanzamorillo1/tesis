package com.example.tesis.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.example.tesis.R
import com.example.tesis.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {
    private var body: String? = null
    private var imageInt: Int = 0
    private var visible: Boolean = false
    private lateinit var binding: FragmentOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            body = it.getString(ARG_PARAM1)
            imageInt = it.getInt(ARG_PARAM2)
            visible = it.getBoolean(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOnboardingBinding.bind(view)
        binding.run {
            description.text = body
            image.setImageDrawable(AppCompatResources.getDrawable(requireContext(), imageInt))
            if (visible) {
                startButton.visibility = View.VISIBLE
                licenseOSM.visibility = View.INVISIBLE
            } else {
                startButton.visibility = View.INVISIBLE
                licenseOSM.visibility = View.VISIBLE
            }
            startButton.setOnClickListener {
                startActivity(Intent(context, CountInputActivity::class.java))
            }
        }
    }

    companion object {
        fun newInstance(body: String, image: Int, visible: Boolean = false) =
            OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, body)
                    putInt(ARG_PARAM2, image)
                    putBoolean(ARG_PARAM3, visible)
                }
            }

        private const val ARG_PARAM1 = "body"
        private const val ARG_PARAM2 = "image"
        private const val ARG_PARAM3 = "visible"
    }
}