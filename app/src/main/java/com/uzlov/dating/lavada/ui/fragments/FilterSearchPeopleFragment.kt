package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.FragmentFiletSearchPeopleBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.domain.models.UserFilter
import com.uzlov.dating.lavada.ui.activities.HostActivity
import com.uzlov.dating.lavada.ui.fragments.profile.FilterLookingForFragment
import javax.inject.Inject

class FilterSearchPeopleFragment :
    BaseFragment<FragmentFiletSearchPeopleBinding>(FragmentFiletSearchPeopleBinding::inflate) {

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

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
        arguments?.let {
            it.getParcelable<User>(NEW_USER)?.let { _user ->
                self = _user.copy()
            }
        }
        viewBinding.tiEtLocation.setText(self.location)
        initListeners()
        updateUiFilter()
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
            slAge.addOnChangeListener { rangeSlider, _, _ ->
                ageStart = rangeSlider.values[0].toInt()
                ageEnd = rangeSlider.values[1].toInt()
                tvAgeValue.text = ageStart.toString() + " - " + ageEnd.toString()
            }
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