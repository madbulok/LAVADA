package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.FragmentShopBinding

class ShopFragment: BaseFragment<FragmentShopBinding>(FragmentShopBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.tvAboutPremiumLabel.setOnClickListener {
            parentFragmentManager.apply {
                beginTransaction()
                    .replace(R.id.container, AboutPremiumFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
        viewBinding.tbBackAction.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    companion object{
        fun newInstance(): ShopFragment {
            val fragment = ShopFragment().apply {
            }
            return fragment
        }
    }
}