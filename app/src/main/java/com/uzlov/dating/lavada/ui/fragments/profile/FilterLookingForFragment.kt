package com.uzlov.dating.lavada.ui.fragments.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.data_sources.IUsersRepository
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.FragmentAboutMyselfBinding
import com.uzlov.dating.lavada.databinding.FragmentLookingForBinding
import com.uzlov.dating.lavada.domain.models.MALE
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.domain.models.UserFilter
import com.uzlov.dating.lavada.ui.activities.LoginActivity
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject

class FilterLookingForFragment :
    BaseFragment<FragmentLookingForBinding>(FragmentLookingForBinding::inflate) {

    @Inject
    lateinit var usersRepository: IUsersRepository

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    private var user = User()
    private var userFilter = UserFilter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        userFilter = preferenceRepository.readFilter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            it.getParcelable<User>(NEW_USER)?.let { _user ->
                user = _user.copy()
            }
        }

        initListeners()
        updateUiFilter()
    }

    private fun updateUiFilter() {
        with(viewBinding) {
            slAge.valueFrom = 18F
            slAge.valueTo = 50F
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
            slAge.valueTo = 22F
            btnNext.setOnClickListener {
                saveLocalFilter()
                (requireActivity() as LoginActivity).startSelectVideo(user)
            }

            btnBack.setOnClickListener {
                (requireActivity() as LoginActivity).rollbackFragment()
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
                ageStart = viewBinding.slAge.valueFrom.toInt(),
                ageEnd = viewBinding.slAge.valueTo.toInt(),
                latitude = 40F,
                longitude = 40F
            )
        )
    }

    companion object {
        private const val NEW_USER = "user"

        fun newInstance() =
            FilterLookingForFragment().apply {
                arguments = Bundle().apply {
                }
            }

        fun newInstance(user: User?) = FilterLookingForFragment().apply {
            arguments = Bundle().apply {
                putParcelable(NEW_USER, user)
            }
        }
    }
}
