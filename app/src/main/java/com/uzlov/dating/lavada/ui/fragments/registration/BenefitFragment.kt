package com.uzlov.dating.lavada.ui.fragments.registration

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.app.insertLink
import com.uzlov.dating.lavada.databinding.FragmentBenefitsBinding
import com.uzlov.dating.lavada.ui.fragments.BaseFragment

class BenefitFragment private constructor() : BaseFragment<FragmentBenefitsBinding>(FragmentBenefitsBinding::inflate) {

    interface ActionListener {
        fun clickCreateAccount()
        fun clickLogIn()
    }

    private var listener: ActionListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding){
            btnLogin.setOnClickListener {
                listener?.clickCreateAccount()
            }
            tvSingInAnother.insertLink("Log in" to View.OnClickListener {
                listener?.clickLogIn()
            })
        }
    }

    fun setActionListener(_listener: ActionListener?){
        listener = _listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener = null
    }

    companion object {
        fun newInstance(): BenefitFragment = BenefitFragment()
    }
}