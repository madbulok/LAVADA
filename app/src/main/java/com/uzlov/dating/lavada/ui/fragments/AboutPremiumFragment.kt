package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.databinding.FragmentAboutPremiumBinding
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentBuyPremium

class AboutPremiumFragment: BaseFragment<FragmentAboutPremiumBinding>(FragmentAboutPremiumBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.tbBackAction.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        viewBinding.btnBuyPremium.setOnClickListener {
            val buyPremiumFragment = FragmentBuyPremium()
            buyPremiumFragment.show(childFragmentManager, buyPremiumFragment.javaClass.simpleName)
        }
    }
}