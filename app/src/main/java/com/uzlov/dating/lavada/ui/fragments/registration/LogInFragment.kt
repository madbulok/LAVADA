package com.uzlov.dating.lavada.ui.fragments.registration

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService.Companion.TAG
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.FragmentLoginBinding
import com.uzlov.dating.lavada.domain.models.AuthorizedUser
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.ui.activities.LoginActivity
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import java.util.regex.Pattern
import javax.inject.Inject

class LogInFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(requireContext(),
            firebaseEmailAuthService.getGSO(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().appComponent.inject(this)

        viewBinding.tilPassword.isEndIconVisible = false
        addTextChangedListener()
        addClickListeners()

        viewBinding.tvLabelForgotPass.setOnClickListener {
            if (!viewBinding.tiEtEmail.text.toString().isNullOrBlank()) {
                showCustomAlert()
            } else
                Toast.makeText(context, "Сначала попытайтесь войти", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addClickListeners() {
        val userLocal = preferenceRepository.readUser()

        viewBinding.btnLogin.setOnClickListener {
            if (!viewBinding.tiEtEmail.text.isNullOrEmpty()) {
                val email = viewBinding.tiEtEmail.text.toString()
                val password = viewBinding.textInputPassword.text.toString()
                if (!email.isNullOrEmpty() && password.isNotEmpty()) {
                    firebaseEmailAuthService.loginWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                val user = User(
                                    uid = firebaseEmailAuthService.auth.currentUser?.uid ?: "",
                                    email = firebaseEmailAuthService.auth.currentUser?.email)

                                preferenceRepository.updateUser(
                                    AuthorizedUser(
                                        uuid = firebaseEmailAuthService.auth.currentUser?.uid ?: "",
                                        datetime = System.currentTimeMillis() / 1000,
                                        name = firebaseEmailAuthService.auth.currentUser?.email ?: "",
                                        isReady = userLocal?.isReady ?: false
                                    )
                                )
                                if(userLocal?.isReady == true){
                                    (requireActivity() as LoginActivity).startHome()
                                } else  (requireActivity() as LoginActivity).startFillDataFragment(user)
                            } else {
                                Toast.makeText(requireContext(), task.exception?.localizedMessage ?: "Ошибка входа. Возможно неверные данные", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

        viewBinding.btnGoogle.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            mainActivityResultLauncher.launch(intent)
        }

        viewBinding.btnFacebook.setOnClickListener {

        }
    }

    private val mainActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    task.getResult(ApiException::class.java)?.let {
                        firebaseEmailAuthService.setToken(it.idToken!!)
                        lifecycleScope.launchWhenResumed {
                            preferenceRepository.updateUser(
                                AuthorizedUser(
                                    uuid = firebaseEmailAuthService.auth.currentUser?.uid ?: "",
                                    datetime = System.currentTimeMillis() / 1000,
                                    name = firebaseEmailAuthService.auth.currentUser?.email ?: "",

                                )
                            )
                            val user = User(
                                uid = firebaseEmailAuthService.auth.currentUser?.uid ?: "",
                                email = firebaseEmailAuthService.auth.currentUser?.email)

                            firebaseEmailAuthService.loginWithGoogleAccount()
                                .addOnCompleteListener(requireActivity()) { _task ->
                                    if (_task.isSuccessful) {
                                        (requireActivity() as LoginActivity).startFillDataFragment(
                                            user)
                                    } else {
                                        Log.w(TAG, "createUserWithGoogle:failure", _task.exception)
                                    }
                                }
                        }
                    }
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                }
            }
        }

    private fun showCustomAlert() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_layout, null)
        val customDialog = context?.let {
            MaterialAlertDialogBuilder(it, R.style.MaterialAlertDialog_rounded)
                .setView(dialogView)
                .show()
        }
        val btDismiss = dialogView.findViewById<TextView>(R.id.btDismissCustomDialog)
        btDismiss.setOnClickListener {
            customDialog?.dismiss()
        }
        val btSendPass = dialogView.findViewById<Button>(R.id.btnSendPasswordCustomDialog)
        btSendPass.setOnClickListener {

            /*
            вот эта штука присылает письмо на почту, оттуда редикретит
            на свой адрес и предлагает там ввести новый пароль. Варианта сделать это внутри приложения
            я чот не нашла(
            */

            val email = viewBinding.tiEtEmail.text.toString()
            if (!email.isNullOrEmpty()) {
                firebaseEmailAuthService.auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Email sent.")
                            customDialog?.dismiss()
                        }
                    }
            }
        }
    }

    private fun addTextChangedListener() {
        viewBinding.tiEtEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                verifyEditText()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
        viewBinding.textInputPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                verifyEditText()
                viewBinding.tilPassword.isEndIconVisible =
                    !viewBinding.textInputPassword.text.isNullOrEmpty()
                if (!isValidPassword(viewBinding.textInputPassword.text.toString())) {
                    viewBinding.tilPassword.error = getString(R.string.error)
                } else viewBinding.tilPassword.isErrorEnabled = false

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun verifyEditText() {
        with(viewBinding) {
            btnLogin.isEnabled = isValidEmail(viewBinding.tiEtEmail.text.toString()) &&
                    isValidPassword(viewBinding.textInputPassword.text.toString())

        }
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

    companion object {
        fun newInstance() =
            LogInFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}