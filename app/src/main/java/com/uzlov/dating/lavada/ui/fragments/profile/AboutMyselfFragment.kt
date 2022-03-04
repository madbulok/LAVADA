package com.uzlov.dating.lavada.ui.fragments.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.data_sources.IUsersRepository
import com.uzlov.dating.lavada.databinding.FragmentAboutMyselfBinding
import com.uzlov.dating.lavada.domain.models.MALE
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.ui.activities.LoginActivity
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.viemodels.GeocodingViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class AboutMyselfFragment :
    BaseFragment<FragmentAboutMyselfBinding>(FragmentAboutMyselfBinding::inflate) {

    @Inject
    lateinit var usersRepository: IUsersRepository

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService
    private var user = User()

    @Inject
    lateinit var factoryModel: ViewModelFactory

    private lateinit var geocodingViewModel: GeocodingViewModel

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startGetLocation()
                } else {
                    requirePermissionsLocation()
                    Toast.makeText(requireContext(),
                        "Вы запретили доступ к геолокации",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private val mFusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private val mSettingsClient: SettingsClient by lazy {
        LocationServices.getSettingsClient(requireContext())
    }

    private val mLocationRequest: LocationRequest by lazy {
        createLocationRequest()
    }

    private val mLocationCallback: LocationCallback by lazy {
        createLocationCallback()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        geocodingViewModel = factoryModel.create(GeocodingViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            it.getParcelable<User>(NEW_USER)?.let { _user ->
                user = _user.copy()
            }
        }

        addTextChangedListener()
        initListeners()

        (requireContext() as LoginActivity).clearFragments()

        checkPermission()
    }

    private fun initListeners() {
        with(viewBinding) {
            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rbMan ->  user.male = MALE.MAN
                    R.id.rvWoman -> user.male = MALE.WOMAN
                    R.id.rbAnother -> user.male = MALE.ANOTHER
                }
            }

            slAge.addOnChangeListener { _, value, _ ->
                user.age = value.toInt()
            }
            btnNext.setOnClickListener {
                user.name = tiEtName.text.toString()
                user.about = tiEtLocation.text.toString()
                user.uid = firebaseEmailAuthService.getUserUid()!!
                if (user.email.isNullOrBlank()) {
                    user.email = firebaseEmailAuthService.auth.currentUser?.email
                }
                usersRepository.putUser(user)
                (requireActivity() as LoginActivity).startSettingsLookInForFragment(user)

            }
            slAge.addOnChangeListener { _, value, _ ->
                tvAgeValue.text = value.toInt().toString()
                progressRegistration.setProgressCompat(50, true)
            }
            swEnableLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (checkPermission()) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            startGetLocation()
                        }
                        user.location = tiEtLocation.text.toString()
                    }
                }
            }
        }
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun createLocationCallback() = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            user.lat = result.lastLocation.latitude
            user.lon = result.lastLocation.longitude
            geocodingViewModel.fetchGeocoding(
                result.lastLocation.latitude.toString(),
                result.lastLocation.longitude.toString()
            ).observe(viewLifecycleOwner, {
                viewBinding.tiEtLocation.setText(it.getViewAddress())
            })
        }
    }

    private fun checkPermission(): Boolean {
        return if (!isGrantedAccessCoarseLocation() || !isGrantedAccessFineLocation()
        ) {
            requirePermissionsLocation()
            false
        } else true
    }

    private fun isGrantedAccessCoarseLocation(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun isGrantedAccessFineLocation(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requirePermissionsLocation() {
        when {
            isGrantedAccessCoarseLocation() || isGrantedAccessFineLocation() -> {
                startGetLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // show message
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun startGetLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, Looper.getMainLooper())

    }

    private fun addTextChangedListener() {
        viewBinding.tiEtName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                verifyEditText()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        viewBinding.tiEtLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                verifyEditText()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun verifyEditText() {
        with(viewBinding) {
            btnNext.isEnabled = !tiEtName.text.isNullOrBlank()
            if (!tiEtName.text.isNullOrBlank()) {
                progressRegistration.setProgressCompat(20, true)
            } else {
                progressRegistration.setProgressCompat(10, true)
            }
        }
    }

    companion object {
        private const val NEW_USER = "user"
        const val REQUEST_EXTERNAL_STORAGE = 1
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

        fun newInstance() =
            AboutMyselfFragment().apply {
                arguments = Bundle().apply {
                }
            }

        fun newInstance(user: User?) = AboutMyselfFragment().apply {
            arguments = Bundle().apply {
                putParcelable(NEW_USER, user)
            }
        }
    }
}
