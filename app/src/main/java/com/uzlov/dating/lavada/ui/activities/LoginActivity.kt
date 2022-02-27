package com.uzlov.dating.lavada.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.LoginActivityBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.ui.fragments.profile.AboutMyselfFragment
import com.uzlov.dating.lavada.ui.fragments.profile.FilterLookingForFragment
import com.uzlov.dating.lavada.ui.fragments.registration.BenefitFragment
import com.uzlov.dating.lavada.ui.fragments.registration.LogInFragment
import com.uzlov.dating.lavada.ui.fragments.registration.RegistrationFragment


class LoginActivity: AppCompatActivity(){
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
                .addToBackStack("logIn")
                .commit()
        }
    }

    private fun showCreateAccountFragment() {
        supportFragmentManager.apply {
            beginTransaction()
                .hide(benefitFragment)
                .hide(loginFragment)
                .show(registrationFragment)
                .addToBackStack("reg")
                .commit()
        }
    }

    private lateinit var aboutMyselfFragment: AboutMyselfFragment

    fun startFillDataFragment(user: User){
        aboutMyselfFragment = AboutMyselfFragment.newInstance(user)
        supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.container, aboutMyselfFragment, "about")
                .commit()
        }
    }

    private lateinit var filterLookingForFragment: FilterLookingForFragment

    fun startSettingsLookInForFragment(user: User){
        filterLookingForFragment =  FilterLookingForFragment.newInstance(user)
        supportFragmentManager.apply {
            beginTransaction()
                .add(R.id.container, filterLookingForFragment)
                .hide(aboutMyselfFragment)
                .show(filterLookingForFragment)
                .addToBackStack("look for")
                .commit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        showBenefits()
    }

    fun rollbackFragment() = supportFragmentManager.popBackStack()

    fun startHome() {
        startActivity(Intent(this@LoginActivity, LoginActivity::class.java))
        finish()
    }
}