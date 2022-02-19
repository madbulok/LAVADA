package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.databinding.FragmentTermOfUseBinding

class TermOfUseFragment : BaseFragment<FragmentTermOfUseBinding>(FragmentTermOfUseBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}