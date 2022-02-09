package com.uzlov.dating.lavada.ui.fragments.profile

import android.os.Bundle
import com.uzlov.dating.lavada.databinding.FragmentAboutMyselfBinding
import com.uzlov.dating.lavada.ui.fragments.BaseFragment

class AboutMyselfFragment :
    BaseFragment<FragmentAboutMyselfBinding>(FragmentAboutMyselfBinding::inflate) {
    companion object {
        fun newInstance() =
            AboutMyselfFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}