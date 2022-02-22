package com.uzlov.dating.lavada.ui.fragments.settings

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.databinding.FragmentBlackListBinding
import com.uzlov.dating.lavada.ui.fragments.BaseFragment

class BlackListFragment: BaseFragment<FragmentBlackListBinding>(FragmentBlackListBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding. tbBackAction.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    companion object {
        fun newInstance() =
            BlackListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}