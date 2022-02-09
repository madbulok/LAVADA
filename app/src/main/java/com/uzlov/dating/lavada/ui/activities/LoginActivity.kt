package com.uzlov.dating.lavada.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.LoginActivityBinding
import com.uzlov.dating.lavada.ui.fragments.registration.RegistrationFragment

class LoginActivity: AppCompatActivity(){
    private var _viewBinding: LoginActivityBinding? = null
    private val viewBinding get() = _viewBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _viewBinding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        supportFragmentManager.apply {
            beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, RegistrationFragment())
                .commit()
        }
    }
}