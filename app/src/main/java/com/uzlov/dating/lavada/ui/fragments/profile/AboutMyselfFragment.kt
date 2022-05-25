package com.uzlov.dating.lavada.ui.fragments.profile

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.RuntimeExecutionException
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.repository.LocationRepository
import com.uzlov.dating.lavada.databinding.FragmentAboutMyselfBinding
import com.uzlov.dating.lavada.domain.models.MALE
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.ui.activities.LoginActivity
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.viemodels.GeocodingViewModel
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject


class AboutMyselfFragment :
    BaseFragment<FragmentAboutMyselfBinding>(FragmentAboutMyselfBinding::inflate) {

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService
    private var user = User()

    @Inject
    lateinit var factoryModel: ViewModelFactory

    @Inject
    lateinit var locationRepository: LocationRepository

    private lateinit var geocodingViewModel: GeocodingViewModel
    private lateinit var userViewModel: UsersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        geocodingViewModel = factoryModel.create(GeocodingViewModel::class.java)
        userViewModel = factoryModel.create(UsersViewModel::class.java)

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
        showLocation()
        initListeners()
        (requireContext() as LoginActivity).clearFragments()
    }

    private fun initListeners() {
        with(viewBinding) {
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbMan -> {
                        user.male = MALE.MAN
                        progressRegistration.setProgressCompat(40, true)
                    }
                    R.id.rvWoman -> {
                        user.male = MALE.WOMAN
                        progressRegistration.setProgressCompat(40, true)
                    }
                    R.id.rbAnother -> {
                        user.male = MALE.ANOTHER
                        progressRegistration.setProgressCompat(40, true)
                    }
                }
            }

            slAge.addOnChangeListener { _, value, _ ->
                user.age = value.toInt()
            }
            btnNext.setOnClickListener {
                user.name = tiEtName.text.toString()
                user.uid = firebaseEmailAuthService.getUserUid()!!

                if (user.email.isNullOrBlank()) {
                    user.email = firebaseEmailAuthService.auth.currentUser?.email
                }

                // специально оставлю ошибку чтоб заменить на рабочий метод
//                userViewModel.updateUser(user)

                (requireActivity() as LoginActivity).startSettingsLookInForFragment(user)

            }
            slAge.addOnChangeListener { _, value, _ ->
                tvAgeValue.text = value.toInt().toString()
                progressRegistration.setProgressCompat(50, true)
            }
            swEnableLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    btnNext.isEnabled = true
                    showLocation()
                } else {
                    viewBinding.tiEtLocation.setText("")
                    btnNext.isEnabled = false
                }
            }
        }
    }

    private fun showLocation(){
        if (checkPermission()) {
            lifecycleScope.launchWhenResumed {
                try {
                    val location = locationRepository.getLocation()
                    viewBinding.btnNext.isEnabled = true
                    viewBinding.swEnableLocation.isChecked = true
                    if (location != null) {
                        startGeocoding(location)
                    } else {
                        val l = locationRepository.requestLocation()
                        startGeocoding(l)
                    }
                } catch (e: RuntimeException) {
                    Toast.makeText(
                        requireContext(),
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
    }

    private fun startGeocoding(location: Location) {
        geocodingViewModel.fetchGeocoding(
            location.latitude.toString(),
            location.longitude.toString()
        ).observe(viewLifecycleOwner, {
            viewBinding.tiEtLocation.setText(it.getViewAddress())
            user.location = it.getViewAddress()
        })

        user.lat = location.latitude
        user.lon = location.longitude
    }


    private fun checkPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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
            btnNext.isEnabled = !tiEtName.text.isNullOrBlank() && swEnableLocation.isChecked
            if (!tiEtName.text.isNullOrBlank()) {
                progressRegistration.setProgressCompat(30, true)
            } else {
                progressRegistration.setProgressCompat(20, true)
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
