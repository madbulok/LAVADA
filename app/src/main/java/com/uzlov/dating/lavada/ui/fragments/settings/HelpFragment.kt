package com.uzlov.dating.lavada.ui.fragments.settings

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.databinding.FragmentHelpBinding
import com.uzlov.dating.lavada.ui.fragments.BaseFragment

class HelpFragment: BaseFragment<FragmentHelpBinding>(FragmentHelpBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.tbBackAction.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    companion object {
        fun newInstance() =
            HelpFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}