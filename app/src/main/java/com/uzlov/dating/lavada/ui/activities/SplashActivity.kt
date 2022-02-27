package com.uzlov.dating.lavada.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Splash экран + проверка авторизован ли пользователь
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_splash_activity)
        appComponent.inject(this)

        // get cached user
        val userLocal = preferenceRepository.readUser()

        if (userLocal != null){
            lifecycleScope.launchWhenResumed {
                delay(1000)
                startActivity(Intent(this@SplashActivity, HostActivity::class.java))
                finish()
            }
        } else {
            lifecycleScope.launchWhenResumed {
                delay(1000)
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}