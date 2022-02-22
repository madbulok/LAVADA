package com.uzlov.dating.lavada.ui.fragments.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.google.firebase.auth.EmailAuthProvider
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService.Companion.TAG
import com.uzlov.dating.lavada.databinding.FragmentUpdatePasswordBinding
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.ui.fragments.registration.LogInFragment
import java.util.regex.Pattern
import javax.inject.Inject

class UpdatePasswordFragment :
    BaseFragment<FragmentUpdatePasswordBinding>(FragmentUpdatePasswordBinding::inflate) {
    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addTextChangedListener()
        val user = firebaseEmailAuthService.auth.currentUser!!

        with(viewBinding) {
            tbBackAction.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            btnSave.setOnClickListener {
                val oldPassword = viewBinding.tiEtOldPassword.text.toString()
                val newPassword = viewBinding.tiEdNewPassword.text.toString()

                //ничего не понимаю. проверка проходит с любым паролем
                //нужно как-то ручками организовывать проверку
                val credential = EmailAuthProvider
                    .getCredential(user.email.toString(), oldPassword)
                user.reauthenticate(credential)
                    .addOnCompleteListener {
                        Log.d(TAG, "User re-authenticated.")
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "User password updated.")
                                    parentFragmentManager.beginTransaction()
                                        .replace(R.id.container, SettingsFragment.newInstance())
                                        .commit()
                                }
                            }
                    }
            }
        }
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

    private fun verifyEditText() {
        with(viewBinding) {
            btnSave.isEnabled = isValidPassword(viewBinding.tiEtOldPassword.text.toString()) &&
                    isValidPassword(viewBinding.tiEdNewPassword.text.toString())
        }
    }

    private fun addTextChangedListener() {
        viewBinding.tiEtOldPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                verifyEditText()
                viewBinding.tilOldPassword.isEndIconVisible =
                    !viewBinding.tiEtOldPassword.text.isNullOrEmpty()
                if (!isValidPassword(viewBinding.tiEtOldPassword.text.toString())) {
                    viewBinding.tilOldPassword.error = getString(R.string.error)
                } else viewBinding.tilOldPassword.isErrorEnabled = false
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
        viewBinding.tiEdNewPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                verifyEditText()
                viewBinding.tilNewPassword.isEndIconVisible =
                    !viewBinding.tiEdNewPassword.text.isNullOrEmpty()
                if (!isValidPassword(viewBinding.tiEdNewPassword.text.toString())) {
                    viewBinding.tilNewPassword.error = getString(R.string.error)
                } else viewBinding.tilNewPassword.isErrorEnabled = false
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    companion object {
        fun newInstance() =
            UpdatePasswordFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}