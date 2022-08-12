package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.RangeSlider
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.FragmentFiletSearchPeopleBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.domain.models.UserFilter
import com.uzlov.dating.lavada.ui.activities.HostActivity
import com.uzlov.dating.lavada.ui.fragments.profile.FilterLookingForFragment
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject

class FilterSearchPeopleFragment :
    BaseFragment<FragmentFiletSearchPeopleBinding>(FragmentFiletSearchPeopleBinding::inflate) {

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    @Inject
    lateinit var factoryViewModel: ViewModelFactory

    private lateinit var model: UsersViewModel

    private lateinit var self: User
    private var userFilter = UserFilter()

    var ageStart = 0
    var ageEnd = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        userFilter = preferenceRepository.readFilter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = factoryViewModel.create(UsersViewModel::class.java)
        arguments?.let {
            it.getParcelable<User>(NEW_USER)?.let { _user ->
                self = _user.copy()
            }
        }
        viewBinding.tiEtLocation.setText(self.location)
        initListeners()
        updateUiFilter()
        initObserve()
    }

    private fun updateUiFilter() {
        with(viewBinding) {
            when (userFilter.sex) {
                0 -> radioGroup.check(R.id.rbMan)
                1 -> radioGroup.check(R.id.rvWoman)
            }

            btnNext.isEnabled = true
        }
    }

    private fun initObserve(){
        model.listUsersData.observe(viewLifecycleOwner) { users ->
           if (users.isEmpty()){
               viewBinding.tvErrorSearch.visibility = View.VISIBLE
               viewBinding.btnNext.isEnabled = false
           } else{
               viewBinding.tvErrorSearch.visibility = View.GONE
               viewBinding.btnNext.isEnabled = true
           }
        }
    }
    private fun initListeners() {
        with(viewBinding) {
            slAge.valueFrom = 18F
            slAge.valueTo = 90F
            reset.setOnClickListener {
                slAge.values = listOf(30F, 50F)
                radioGroup.clearCheck()
            }
            btnNext.setOnClickListener {
                saveLocalFilter()
                (requireActivity() as HostActivity).openFragment()
            }
            tbBackAction.setOnClickListener {
                (requireActivity() as HostActivity).rollbackFragment()
            }
            slAge.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {
                    val values = slAge.values
                    //Those are the satrt and end values of sldier when user start dragging
                    Log.i("SliderPreviousValue From", values[0].toString())
                    Log.i("SliderPreviousValue To", values[1].toString())
                }

                override fun onStopTrackingTouch(slider: RangeSlider) {
                    ageStart = slider.values[0].toInt()
                    ageEnd = slider.values[1].toInt()
                    saveLocalFilter()
                }
            })

            slAge.addOnChangeListener { rangeSlider, _, _ ->
                ageStart = rangeSlider.values[0].toInt()
                ageEnd = rangeSlider.values[1].toInt()
                tvAgeValue.text = ageStart.toString() + " - " + ageEnd.toString()
            }
            swPremium.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    showCustomAlertComingSoon()
                    swPremium.isChecked = false
                }

            }
        }
    }

    private fun showCustomAlertComingSoon() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_coming_soon, null)
        val customDialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_rounded)
                .setView(dialogView)
                .show()

        dialogView.findViewById<TextView>(R.id.header).text =
            getString(R.string.ficha_is_coming_soon)
        val btSendPass = dialogView.findViewById<Button>(R.id.btnSendPasswordCustomDialog)
        btSendPass.text = getString(R.string.i_ll_be_waiting)
        btSendPass.setOnClickListener {
            customDialog?.dismiss()
        }
    }

    private fun saveLocalFilter() {
        val sex = when (viewBinding.radioGroup.checkedRadioButtonId) {
            R.id.rbMan -> 0
            R.id.rvWoman -> 1
            else -> 0
        }
        preferenceRepository.setFilter(
            UserFilter(
                sex = sex,
                ageStart = ageStart,
                ageEnd = ageEnd,
                latitude = 40F,
                longitude = 40F
            )
        )
        firebaseEmailAuthService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
            model.getUsers(tokenFb.token.toString(), preferenceRepository.readFilter().sex, preferenceRepository.readFilter().ageStart.toString(), preferenceRepository.readFilter().ageEnd.toString())
        }
    }

    companion object {
        private const val NEW_USER = "user"

        fun newInstance(user: User) =
            FilterSearchPeopleFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(NEW_USER, user)
                }
            }
    }
}