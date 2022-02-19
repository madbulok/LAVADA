package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.databinding.FragmentPrivatePoliceBinding

class PrivatePolicyFragment : BaseFragment<FragmentPrivatePoliceBinding>(FragmentPrivatePoliceBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}