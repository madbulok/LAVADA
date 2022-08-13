package com.uzlov.dating.lavada.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.ui.adapters.PlayerViewAdapter
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Splash экран + проверка авторизован ли пользователь
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_splash_activity)
        appComponent.inject(this)

        // get cached user
        val userLocal = preferenceRepository.readUser()

        if (preferenceRepository.readTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (!firebaseEmailAuthService.getUserUid().isNullOrEmpty()) {
            if (userLocal?.isReady == true) {
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
        } else {
            lifecycleScope.launchWhenResumed {
                delay(1000)
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun loadImage(container: ImageView) {
        Glide
            .with(this)
            .load(R.drawable.bubble_background)
            .error(R.drawable.ic_default_user)
            .into(container)
    }

    override fun onStop() {
        super.onStop()
        PlayerViewAdapter.releaseAllPlayers()
    }
}