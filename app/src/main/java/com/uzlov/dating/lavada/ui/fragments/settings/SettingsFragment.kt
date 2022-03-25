package com.uzlov.dating.lavada.ui.fragments.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.FragmentSettingsBinding
import com.uzlov.dating.lavada.ui.activities.HostActivity
import com.uzlov.dating.lavada.ui.activities.LoginActivity
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import javax.inject.Inject

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {
    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        with(viewBinding) {
            swTheme.isChecked = preferenceRepository.readTheme()
            swPremium.isChecked = preferenceRepository.readPremiumVisible()
        }

    }

    private fun initListeners() {
        with(viewBinding) {
            tbBackAction.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            btnLogOut.setOnClickListener {
                firebaseEmailAuthService.logout()
                preferenceRepository.clearUser()
                startLogin()
            }
            btnDel.setOnClickListener {
                showCustomAlertToDelAcc()
            }
            swTheme.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    preferenceRepository.setTheme(isChecked)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    preferenceRepository.setTheme(isChecked)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                }

            }
            swPremium.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    showCustomAlertToByPremium()
                    swPremium.isChecked = false
                }
            }
            btnPassword.setOnClickListener {
                updateUI(UpdatePasswordFragment.newInstance())
            }
            btnBlackList.setOnClickListener {
                updateUI(BlackListFragment.newInstance())
            }
            btnNotifications.setOnClickListener {
                updateUI(NotificationsFragment.newInstance())
            }
            btnHelp.setOnClickListener {
                updateUI(HelpFragment.newInstance())
            }
        }
    }

    private fun showCustomAlertToDelAcc() {
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
        dialogView.findViewById<TextView>(R.id.header).text =
            getString(R.string.del_account_header)
        dialogView.findViewById<TextView>(R.id.description).text =
            getString(R.string.del_account_desc)
        val btSendPass = dialogView.findViewById<Button>(R.id.btnSendPasswordCustomDialog)
        btSendPass.text = getString(R.string.delete)
        btSendPass.setOnClickListener {
            if (firebaseEmailAuthService.auth.currentUser != null) {
                firebaseEmailAuthService.delUser()
                preferenceRepository.clearUser()
                //вероятно тут еще и из базы удалять все нужно будет
                startLogin()
            }
            customDialog?.dismiss()
        }
    }

    private fun showCustomAlertToByPremium() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_layout, null)
        val customDialog = context?.let {
            MaterialAlertDialogBuilder(it, R.style.MaterialAlertDialog_rounded)
                .setView(dialogView)
                .show()
        }
        dialogView.findViewById<TextView>(R.id.btDismissCustomDialog).visibility = View.GONE
        dialogView.findViewById<TextView>(R.id.description).visibility = View.GONE

        dialogView.findViewById<TextView>(R.id.header).text =
            getString(R.string.only_for_premium_header)

        val btSendPass = dialogView.findViewById<Button>(R.id.btnSendPasswordCustomDialog)
        btSendPass.text = getString(R.string.buy_premium)
        btSendPass.setOnClickListener {
            Toast.makeText(context, "Покупаем премиум", Toast.LENGTH_SHORT).show()
            customDialog?.dismiss()

        }
    }

    private fun updateUI(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun startLogin() {
        startActivity(Intent(context, LoginActivity::class.java))
        (requireActivity() as HostActivity).finish()
    }

    companion object {
        fun newInstance() =
            SettingsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}