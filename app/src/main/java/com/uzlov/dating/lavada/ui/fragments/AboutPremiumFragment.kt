package com.uzlov.dating.lavada.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.FragmentAboutPremiumBinding
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentBuyPremium

class AboutPremiumFragment: BaseFragment<FragmentAboutPremiumBinding>(FragmentAboutPremiumBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       loadImage(resources.getDrawable(R.drawable.about_premium), viewBinding.ivAboutPremium)
        viewBinding.btnBuyPremium.setOnClickListener {
            val buyPremiumFragment = FragmentBuyPremium()
            buyPremiumFragment.show(childFragmentManager, buyPremiumFragment.javaClass.simpleName)
        }
    }

    private fun loadImage(image: Drawable, container: ImageView) {
        view?.let {
            Glide
                .with(it.context)
                .load(image)
                .error(R.drawable.ic_default_user)
                .into(container)
        }
    }
}