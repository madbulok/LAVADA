package com.uzlov.dating.lavada.ui.fragments.registration

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentLoginBinding
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.ui.fragments.MainVideosFragment
import javax.inject.Inject

class LogInFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().appComponent.inject(this)

        viewBinding.btnLogin.setOnClickListener {
            val email = viewBinding.tiEtEmail.text.toString()
            val password = viewBinding.textInputPassword.text.toString()
            if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
                firebaseEmailAuthService.loginWithEmailAndPassword(
                    email,
                    password,
                    parentFragmentManager,
                    MainVideosFragment.newInstance()
                )
            }
        }
    }

    companion object {
        fun newInstance() =
            LogInFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}