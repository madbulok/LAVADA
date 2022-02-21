package com.uzlov.dating.lavada.ui.fragments.registration

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService.Companion.TAG
import com.uzlov.dating.lavada.databinding.FragmentLoginBinding
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.ui.fragments.MainVideosFragment
import java.util.regex.Pattern
import javax.inject.Inject

class LogInFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService
    lateinit var email: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().appComponent.inject(this)

        viewBinding.tilPassword.isEndIconVisible = false
        addTextChangedListener()
        viewBinding.btnLogin.setOnClickListener {
            /*
            И вот тут вообще непонятно, отсюда мыло брать или будет какое-то еще поле
            для ввода пароля
            */
            email = viewBinding.tiEtEmail.text.toString()
            val password = viewBinding.textInputPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseEmailAuthService.loginWithEmailAndPassword(
                    email,
                    password,
                    parentFragmentManager,
                    MainVideosFragment.newInstance()
                )
            }
        }

        viewBinding.tvLabelForgotPass.setOnClickListener {
            if (!viewBinding.tiEtEmail.text.toString().isNullOrBlank()){
                showCustomAlert()
            } else
                Toast.makeText(context, "Сначала попытайтесь войти", Toast.LENGTH_SHORT).show()

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

            firebaseEmailAuthService.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Email sent.")
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
                    viewBinding.textInputPassword.error = getString(R.string.error)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
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