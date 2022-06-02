package com.uzlov.dating.lavada.ui.fragments.profile

import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.RuntimeExecutionException
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.repository.LocationRepository
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.FragmentLookingForBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.domain.models.UserFilter
import com.uzlov.dating.lavada.ui.activities.LoginActivity
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.viemodels.GeocodingViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject

class FilterLookingForFragment :
    BaseFragment<FragmentLookingForBinding>(FragmentLookingForBinding::inflate) {


    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    @Inject
    lateinit var factoryModel: ViewModelFactory

    @Inject
    lateinit var locationRepository: LocationRepository

    private lateinit var geocodingViewModel: GeocodingViewModel

    private var user = User()
    private var userFilter = UserFilter()
    var ageStart = 0
    var ageEnd = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        geocodingViewModel = factoryModel.create(GeocodingViewModel::class.java)
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
        showLocation()
    }

    private fun updateUiFilter() {
        with(viewBinding) {
            when (userFilter.sex) {
                0 -> radioGroup.check(R.id.rbMan)
                1 -> radioGroup.check(R.id.rvWoman)
  //              2 -> radioGroup.check(R.id.rbAnother)
            }

            btnNext.isEnabled = true
        }
    }

    private fun initListeners() {
        with(viewBinding) {
            btnNext.setOnClickListener {
                saveLocalFilter()
                (requireActivity() as LoginActivity).startSelectVideo(user)
            }

            btnBack.setOnClickListener {
                (requireActivity() as LoginActivity).rollbackFragment()
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
    private fun showLocation(){
            lifecycleScope.launchWhenResumed {
                try {
                    val location = locationRepository.getLocation()
                    if (location != null) {
                        startGeocoding(location)
                    } else {
                        val l = locationRepository.requestLocation()
                        startGeocoding(l)
                    }
                } catch (e: RuntimeException) {
                    Toast.makeText(
                        context,
                        e.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: RuntimeExecutionException) {
                    Toast.makeText(
                        requireContext(),
                        "Ваше устройство не поддерживает Google Services",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            user.location = viewBinding.tiEtLocation.text.toString()
    }

    private fun startGeocoding(location: Location) {

        geocodingViewModel.fetchGeocoding(
            location.latitude.toString(),
            location.longitude.toString()
        ).observe(viewLifecycleOwner, {
            viewBinding.tiEtLocation.setText(it.getCity())
            user.location = it.getViewAddress()
        })

        user.lat = location.latitude
        user.lon = location.longitude
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
