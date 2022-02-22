package com.uzlov.dating.lavada.ui.fragments.settings

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.databinding.FragmentNotificationsBinding
import com.uzlov.dating.lavada.ui.fragments.BaseFragment

class NotificationsFragment :
    BaseFragment<FragmentNotificationsBinding>(FragmentNotificationsBinding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding) {
            tbBackAction.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

    companion object {
        fun newInstance() =
            NotificationsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}