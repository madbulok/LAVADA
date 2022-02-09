package com.uzlov.dating.lavada.ui.fragments.registration

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
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
import java.util.regex.Pattern
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
        checkUser()
        addTextChangedListener()
        googleSignInClient =
            GoogleSignIn.getClient(requireContext(), firebaseEmailAuthService.getGSO(context!!))

        viewBinding.btnLogin.setOnClickListener {
            val email = viewBinding.textInputEmail.text.toString()
            val password = viewBinding.textInputPassword.text.toString()
            firebaseEmailAuthService.registered(email, password)
            updateUI()
        }
        viewBinding.btnLoginWithGoogle.setOnClickListener {
            onAct()
        }
    }

    private fun checkUser() {
        val currentUser = firebaseEmailAuthService.auth.currentUser
        if (currentUser != null) {
            updateUI()
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

    private fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordREGEX = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[a-zA-Z])" +
                    "(?=\\S+$)" +
                    ".{8,}" +
                    "$"
        )
        return passwordREGEX.matcher(password).matches()
    }

    private fun addTextChangedListener() {
        viewBinding.textInputEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                verifyEditText()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        viewBinding.textInputPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                verifyEditText()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun verifyEditText() {
        with(viewBinding) {
            btnLogin.isEnabled = isValidEmail(viewBinding.textInputEmail.text.toString()) &&
                    isValidPassword(viewBinding.textInputPassword.text.toString())
        }
    }
}
