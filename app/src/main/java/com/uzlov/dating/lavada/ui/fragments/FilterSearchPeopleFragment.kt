package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import com.google.android.material.slider.RangeSlider
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.data_sources.IUsersRepository
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.FragmentFiletSearchPeopleBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.domain.models.UserFilter
import com.uzlov.dating.lavada.ui.activities.HostActivity
import javax.inject.Inject

class FilterSearchPeopleFragment :
    BaseFragment<FragmentFiletSearchPeopleBinding>(FragmentFiletSearchPeopleBinding::inflate) {

    @Inject
    lateinit var usersRepository: IUsersRepository

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

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
        initListeners()
        updateUiFilter()
    }

    private fun updateUiFilter() {
        with(viewBinding) {
            when (userFilter.sex) {
                0 -> radioGroup.check(R.id.rbMan)
                1 -> radioGroup.check(R.id.rvWoman)
                2 -> radioGroup.check(R.id.rbAnother)
            }

            btnNext.isEnabled = true
        }
    }

    private fun initListeners() {
        with(viewBinding) {
            slAge.valueFrom = 18F
            slAge.valueTo = 70F
            btnNext.setOnClickListener {
                saveLocalFilter()
                (requireActivity() as HostActivity).openFragment()
            }
            btnBack.setOnClickListener {
                (requireActivity() as HostActivity).rollbackFragment()
            }
            slAge.addOnChangeListener { rangeSlider, _, _ ->
                ageStart = rangeSlider.values[0].toInt()
                ageEnd = rangeSlider.values[1].toInt()
            }
        }
    }

    private fun saveLocalFilter() {
        val sex = when (viewBinding.radioGroup.checkedRadioButtonId) {
            R.id.rbMan -> 0
            R.id.rvWoman -> 1
            R.id.rbAnother -> 2
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
        fun newInstance() =
            FilterSearchPeopleFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}