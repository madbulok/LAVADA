package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.FragmentShopBinding
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentBuyCoins

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
        viewBinding.btnBuyCoins.setOnClickListener {
            val buyCoinsFragment = FragmentBuyCoins()
            buyCoinsFragment.show(childFragmentManager, buyCoinsFragment.javaClass.simpleName)
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