package com.uzlov.dating.lavada.ui.fragments.settings

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentSettingsBinding
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.ui.fragments.profile.ProfileFragment
import javax.inject.Inject

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {
    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding) {
            btnLogOut.setOnClickListener {
                firebaseEmailAuthService.logout()
                Toast.makeText(context, "Вы успешно вышли из аккаунта", Toast.LENGTH_SHORT).show()

            }
            btnDel.setOnClickListener {
                showCustomAlertToDelAcc()
            }
            swPremium.setOnCheckedChangeListener{ _, isChecked ->
                if (isChecked){
                    showCustomAlertToByPremium()
                    swPremium.isChecked = false
                }
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
        dialogView.findViewById<TextView>(R.id.description).text = getString(R.string.del_account_desc)
        val btSendPass = dialogView.findViewById<Button>(R.id.btnSendPasswordCustomDialog)
        btSendPass.text = getString(R.string.delete)
        btSendPass.setOnClickListener {
            if (firebaseEmailAuthService.auth.currentUser != null) {
                firebaseEmailAuthService.delUser()
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

    companion object {
        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}