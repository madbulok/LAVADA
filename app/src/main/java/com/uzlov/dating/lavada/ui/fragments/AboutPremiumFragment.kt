package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.uzlov.dating.lavada.databinding.FragmentAboutPremiumBinding

class AboutPremiumFragment: BaseFragment<FragmentAboutPremiumBinding>(FragmentAboutPremiumBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.tbBackAction.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        viewBinding.btnBuyPremium.setOnClickListener {
            Toast.makeText(context, "Покупаем премиум", Toast.LENGTH_SHORT).show()
        }
    }
}