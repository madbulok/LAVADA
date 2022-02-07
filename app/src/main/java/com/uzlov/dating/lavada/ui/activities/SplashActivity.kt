package com.uzlov.dating.lavada.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.uzlov.dating.lavada.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_splash_activity)
        animateStars()
    }

    fun animateStars(){

    }
}