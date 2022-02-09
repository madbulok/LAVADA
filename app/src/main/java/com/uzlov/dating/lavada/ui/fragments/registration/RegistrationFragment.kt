package com.uzlov.dating.lavada.ui.fragments.registration

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService.Companion.TAG
import com.uzlov.dating.lavada.auth.User
import com.uzlov.dating.lavada.databinding.FragmentRegistrationBinding
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.ui.fragments.profile.AboutMyselfFragment
import javax.inject.Inject


class RegistrationFragment :
    BaseFragment<FragmentRegistrationBinding>(FragmentRegistrationBinding::inflate) {

    private lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    lateinit var user: User

    private val mainActivityResultLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseEmailAuthService.firebaseAuthWithGoogle(account.idToken!!)
                    updateUI()
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().appComponent.inject(this)
        googleSignInClient =
            GoogleSignIn.getClient(requireContext(), firebaseEmailAuthService.getGSO(context!!))

        viewBinding.btnLogin.setOnClickListener {
            val email = viewBinding.textInputEmail.text.toString()
            val password = viewBinding.textInputPassword.text.toString()
            firebaseEmailAuthService.registered(email, password)
        }
        viewBinding.btnLoginWithGoogle.setOnClickListener {
            onAct()
        }
    }

    private fun onAct() {
        val intent = googleSignInClient.signInIntent
        mainActivityResultLauncher.launch(intent)
    }

    private fun updateUI() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, AboutMyselfFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }
}