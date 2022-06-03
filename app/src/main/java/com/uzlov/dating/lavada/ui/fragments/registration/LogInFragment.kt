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
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import java.util.regex.Pattern
import javax.inject.Inject

class LogInFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    @Inject
    lateinit var authService: FirebaseEmailAuthService

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    @Inject
    lateinit var factoryViewModel: ViewModelFactory

    private lateinit var model: UsersViewModel
    private var self = User()

    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(
            requireContext(),
            authService.getGSO(requireContext())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        model = factoryViewModel.create(UsersViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.tilPassword.isEndIconVisible = false
        addTextChangedListener()
        addClickListeners()

        viewBinding.tvLabelForgotPass.setOnClickListener {
            if (!viewBinding.tiEtEmail.text.toString().isNullOrBlank()) {
                showCustomAlert()
            } else {
                Toast.makeText(requireContext(), "Сначала попытайтесь войти", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun addClickListeners() {
        viewBinding.btnBack.setOnClickListener {
            (requireActivity() as LoginActivity).showBenefits()
        }
        viewBinding.btnLogin.setOnClickListener {
            viewBinding.btnBack.visibility = View.GONE
            viewBinding.btnLogin.visibility = View.GONE
            viewBinding.progressBar.visibility = View.VISIBLE
            if (!viewBinding.tiEtEmail.text.isNullOrEmpty()) {
                val email = viewBinding.tiEtEmail.text.toString()
                val password = viewBinding.textInputPassword.text.toString()

                if (!email.isNullOrEmpty() && password.isNotEmpty()) {
                    authService.loginWithEmailAndPassword(email, password)
                        .addOnSuccessListener(requireActivity()) { _ ->
                            authService.getUser()?.getIdToken(true)
                                ?.addOnSuccessListener { tokenFb ->
                            model.authRemoteUser(hashMapOf("token" to tokenFb.token))
                                .observe(viewLifecycleOwner) { tokenBack ->
                                    model.getUser(tokenBack)
                                        .observe(viewLifecycleOwner) { result ->
                                            self = result?.copy()!!
                                           val savedSelf = preferenceRepository.readUser()
                                            self.ready = savedSelf?.isReady ?: false
                                            val user = User(
                                                uid = authService.auth.currentUser?.uid ?: "",
                                                email = authService.auth.currentUser?.email
                                            )

                                            AuthorizedUser(
                                                uuid = authService.auth.currentUser?.uid ?: "",
                                                datetime = System.currentTimeMillis() / 1000,
                                                name = authService.auth.currentUser?.email ?: "",
                                                isReady = self.ready
                                            ).also { prefUser ->
                                                preferenceRepository.updateUser(prefUser)
                                            }

                                            if (self.ready) {
                                                (requireActivity() as LoginActivity).startHome()
                                            } else {
                                                (requireActivity() as LoginActivity)
                                                    .startFillDataFragment(user)
                                            }
                                        }
                                }
                            }
                        }.addOnFailureListener { error ->
                            error.printStackTrace()
                            Toast.makeText(
                                requireContext(),
                                error.localizedMessage
                                    ?: "Ошибка входа. Возможно неверные данные",
                                Toast.LENGTH_SHORT
                            ).show()
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
                (requireActivity() as LoginActivity).startHome()
                try {
                    task.getResult(ApiException::class.java)?.let {
                        authService.setToken(it.idToken!!)
                        preferenceRepository.updateUser(
                            AuthorizedUser(
                                uuid = authService.auth.currentUser?.uid ?: "",
                                datetime = System.currentTimeMillis() / 1000,
                                name = authService.auth.currentUser?.email ?: ""
                            )
                        )
                        authService.loginWithGoogleAccount()
                            .addOnSuccessListener(requireActivity()) { _ ->

                                authService.getUser()?.getIdToken(true)
                                    ?.addOnSuccessListener { tokenFb ->
                                        model.authRemoteUser(hashMapOf("token" to tokenFb.token))
                                            .observe(this) { tokenBack ->
                                                model.getUser(tokenBack)
                                                    .observe(viewLifecycleOwner) { result ->
                                                        self = result?.copy()!!
                                                        val user = User(
                                                            uid = authService.auth.currentUser?.uid
                                                                ?: "",
                                                            email = authService.auth.currentUser?.email
                                                        )

                                                        AuthorizedUser(
                                                            uuid = authService.auth.currentUser?.uid
                                                                ?: "",
                                                            datetime = System.currentTimeMillis() / 1000,
                                                            name = authService.auth.currentUser?.email
                                                                ?: "",
                                                            isReady = self.ready
                                                        ).also { prefUser ->
                                                            preferenceRepository.updateUser(prefUser)
                                                        }
                                                        if (self.ready) {
                                                            (requireActivity() as LoginActivity).startHome()
                                                        } else {
                                                            (requireActivity() as LoginActivity)
                                                                .startFillDataFragment(user)
                                                        }
                                                    }
                                            }
                                    }
                            }.addOnFailureListener { error ->
                                error.printStackTrace()
                                Toast.makeText(
                                    requireContext(),
                                    error.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun showCustomAlert() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_layout, null)
        val customDialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_rounded)
                .setView(dialogView)
                .show()

        val btDismiss = dialogView.findViewById<TextView>(R.id.btDismissCustomDialog)
        btDismiss.setOnClickListener {
            customDialog?.dismiss()
        }
        val btSendPass = dialogView.findViewById<Button>(R.id.btnSendPasswordCustomDialog)
        btSendPass.setOnClickListener {

            /**
            вот эта штука присылает письмо на почту, оттуда редикретит
            на свой адрес и предлагает там ввести новый пароль. Варианта сделать это внутри приложения
            я чот не нашла(
            */

            val email = viewBinding.tiEtEmail.text.toString()
            if (!email.isNullOrEmpty()) {
                authService.auth.sendPasswordResetEmail(email)
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