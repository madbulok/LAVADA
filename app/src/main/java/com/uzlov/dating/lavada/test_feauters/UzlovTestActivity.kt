package com.uzlov.dating.lavada.test_feauters

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.ui.fragments.MainVideosFragment

class UzlovTestActivity : AppCompatActivity() {
    val fragment = MainVideosFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_layout)

//        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
//        Log.e(javaClass.simpleName, "onCreate: ${applicationContext.cacheDir}")


    }
}