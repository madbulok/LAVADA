package com.uzlov.dating.lavada.ui.fragments.profile

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentProfileBinding
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.ui.fragments.registration.LogInFragment
import com.uzlov.dating.lavada.ui.fragments.registration.RegistrationFragment
import javax.inject.Inject

class ProfileFragment: BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding){
            tvName.text = firebaseEmailAuthService.auth.currentUser?.email
            btnLogOut.setOnClickListener {
                firebaseEmailAuthService.logoutAndUpdateUI(parentFragmentManager, LogInFragment.newInstance())
            }
            btnDel.setOnClickListener {
                firebaseEmailAuthService.delUser(parentFragmentManager, RegistrationFragment())
            }
        }

    }
    companion object {
        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}