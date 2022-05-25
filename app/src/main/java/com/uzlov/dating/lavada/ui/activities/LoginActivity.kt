package com.uzlov.dating.lavada.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.video.OutputResults
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.LoginActivityBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.ui.adapters.PlayerViewAdapter
import com.uzlov.dating.lavada.ui.fragments.VideoCaptureFragment
import com.uzlov.dating.lavada.ui.fragments.profile.AboutMyselfFragment
import com.uzlov.dating.lavada.ui.fragments.profile.FilterLookingForFragment
import com.uzlov.dating.lavada.ui.fragments.profile.PreviewVideoFragment
import com.uzlov.dating.lavada.ui.fragments.profile.UploadVideoFragment
import com.uzlov.dating.lavada.ui.fragments.registration.BenefitFragment
import com.uzlov.dating.lavada.ui.fragments.registration.LogInFragment
import com.uzlov.dating.lavada.ui.fragments.registration.RegistrationFragment


class LoginActivity : AppCompatActivity() {
    private var _viewBinding: LoginActivityBinding? = null
    private val viewBinding get() = _viewBinding!!

    private val callbackAction = object : BenefitFragment.ActionListener {
        override fun clickCreateAccount() {
            showCreateAccountFragment()
        }

        override fun clickLogIn() {
            showLogInFragment()
        }
    }

    private val benefitFragment by lazy { BenefitFragment.newInstance() }.also {
        it.value.setActionListener(callbackAction)
    }

    private val loginFragment by lazy { LogInFragment() }
    private val registrationFragment by lazy { RegistrationFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
 //       setFullscreen()
        _viewBinding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initFragments()
        showBenefits()
    }

    private fun initFragments() {
        supportFragmentManager.beginTransaction()
            .add(R.id.container, benefitFragment).show(benefitFragment)
            .add(R.id.container, loginFragment).hide(loginFragment)
            .add(R.id.container, registrationFragment).hide(registrationFragment)
            .setReorderingAllowed(true)
            .commit()
    }

    private fun showBenefits() {
        supportFragmentManager.apply {
            beginTransaction()
                .show(benefitFragment)
                .hide(registrationFragment)
                .hide(loginFragment)
                .commit()
        }
    }

    private fun showLogInFragment() {
        supportFragmentManager.apply {
            beginTransaction()
                .hide(benefitFragment)
                .hide(registrationFragment)
                .show(loginFragment)
                .addToBackStack("null")
                .commit()
        }
    }

    private fun showCreateAccountFragment() {
        supportFragmentManager.apply {
            beginTransaction()
                .hide(benefitFragment)
                .hide(loginFragment)
                .show(registrationFragment)
                .addToBackStack("null")
                .commit()
        }
    }

    private lateinit var aboutMyselfFragment: AboutMyselfFragment

    fun startFillDataFragment(user: User) {
        aboutMyselfFragment = AboutMyselfFragment.newInstance(user)
        supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.container, aboutMyselfFragment, "null")
                .commit()
        }
    }


    fun startSettingsLookInForFragment(user: User) {
        supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.container, FilterLookingForFragment.newInstance(user))
                .addToBackStack("null")
                .commit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportFragmentManager.popBackStack()
        showBenefits()
    }

    fun rollbackFragment() = supportFragmentManager.popBackStack()

    fun startHome() {
        startActivity(Intent(this@LoginActivity, HostActivity::class.java))
        finish()
    }

    fun clearFragments() {
        supportFragmentManager.clearBackStack("null")
        for (i in 0..supportFragmentManager.backStackEntryCount) supportFragmentManager.popBackStack()
    }

    fun startSelectVideo(user: User) {
        supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.container, UploadVideoFragment.newInstance(user))
                .addToBackStack("null")
                .commit()
        }
    }

    fun showPreviewVideo(_path: String, user: User) {
        supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.fullScreen_container, PreviewVideoFragment.newInstance(_path, user))
                .addToBackStack("null")
                .commit()
        }
    }

    fun routeToMainScreen() {
        startActivity(
            Intent(
                this, HostActivity::class.java
            )
        )
        clearFragments()
        finish()
    }

    override fun onStop() {
        super.onStop()
        PlayerViewAdapter.releaseAllPlayers()
    }


    fun startCaptureVideoFragment(user: User) {

        val videoCaptureCallback =
            object : VideoCaptureFragment.VideoRecordingListener {
                override fun start() {
                    // need disable all transactions
                }

                override fun finish(result: OutputResults) {
                    showPreviewVideo(result.outputUri.toString(), user)
                }

                override fun error(message: String) {
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                }
            }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fullScreen_container, VideoCaptureFragment(videoCaptureCallback))
            .addToBackStack(("null"))
            .commit()
    }

    private fun setFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
    }
}