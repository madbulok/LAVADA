package com.uzlov.dating.lavada.ui.fragments.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.gms.tasks.RuntimeExecutionException
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        geocodingViewModel = factoryModel.create(GeocodingViewModel::class.java)

        checkPermission()
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
    }

    private fun initListeners() {
        with(viewBinding) {
            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rbMan -> user.male = MALE.MAN
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
                if (isChecked){
                    if (checkPermission()){
                        lifecycleScope.launch {
                            getLocation(requireContext(), 1)
                            getLocationLast(requireContext())
                        }

                        user.location = tiEtLocation.text.toString()
                    }
                } else {
                    viewBinding.tiEtLocation.setText("")
                }
            }
        }
    }
    private val locationRequestGPS by lazy {
        LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setNumUpdates(1)
            .setExpirationDuration(1000)
    }

    private val locationRequestNETWORK by lazy {
        LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_LOW_POWER)
            .setNumUpdates(1)
            .setExpirationDuration(1000)
    }

    private suspend fun getLocation(context: Context, offsetMinutes: Int): Location? = suspendCoroutine { task ->
        val ctx = context.applicationContext
        if (!checkPermission()) {
            task.resume(null)
        } else {
            val manager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!LocationManagerCompat.isLocationEnabled(manager)) {
                task.resume(null)
            } else {
                val service = LocationServices.getFusedLocationProviderClient(ctx)
                service.lastLocation
                    .addOnCompleteListener { locTask ->
                        try {
                            if (locTask.result == null
                                || System.currentTimeMillis() - locTask.result!!.time > offsetMinutes
                            )
                            {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    geocodingViewModel.fetchGeocoding(
                                        locTask.result.latitude.toString(),
                                        locTask.result.longitude.toString()
                                    ).observe(viewLifecycleOwner, {
                                        viewBinding.tiEtLocation.setText(it.getViewAddress())
                                        user.location = it.getViewAddress()
                                    })

                                    user.lat = locTask.result.latitude
                                    user.lon = locTask.result.longitude
                                    task.resume(locationRequest(manager, service))
                                }
                            } else {
                                task.resume(locTask.result)
                            }
                        } catch (e: RuntimeExecutionException){
                            Toast.makeText(requireContext(), "Ваше устройство не поддерживает Google Services", Toast.LENGTH_SHORT).show()
                        }

                    }
            }
        }
    }

    private suspend fun getLocationLast(context: Context): Location? = suspendCoroutine { task ->
        val ctx = context.applicationContext
        if (!checkPermission()) {
            task.resume(null)
        } else {
            if (!LocationManagerCompat.isLocationEnabled(ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager)) {
                task.resume(null)
            } else {
                LocationServices.getFusedLocationProviderClient(ctx)
                    .lastLocation
                    .addOnCompleteListener { locTask ->
                        task.resume(locTask.result)
                    }
            }
        }
    }

    private suspend fun locationRequest(locationManager: LocationManager, service: FusedLocationProviderClient): Location? = suspendCoroutine { task ->
        val callback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                service.removeLocationUpdates(this)
                task.resume(p0.lastLocation)
            }
        }
        when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                if (checkPermission()) {
                    service.requestLocationUpdates(locationRequestGPS, callback, Looper.getMainLooper())
                }
            }
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> {
                service.requestLocationUpdates(locationRequestNETWORK, callback, Looper.getMainLooper())
            }
            else -> {
                task.resume(null)
            }
        }
    }


    private fun checkPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE
            )
            false
        } else true
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
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
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
