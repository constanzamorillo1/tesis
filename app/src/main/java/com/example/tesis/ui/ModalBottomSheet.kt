package com.example.tesis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tesis.databinding.FragmentDataPathBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheet: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDataPathBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataPathBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.time.text = "Tiempo estimado: ${requireArguments().getString("time")} minutos"
        binding.kms.text = "Cantidad de kilometros: ${requireArguments().getString("kms")} kms"
    }

    companion object {
        fun newInstance(time: String, kms: String) = ModalBottomSheet().apply {
            arguments = Bundle().apply {
                putString("time", time)
                putString("kms", kms)
            }
        }
    }
}