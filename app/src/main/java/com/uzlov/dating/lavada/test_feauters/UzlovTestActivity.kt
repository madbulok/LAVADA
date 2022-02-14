package com.uzlov.dating.lavada.test_feauters

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.ui.fragments.MainVideosFragment

class UzlovTestActivity : AppCompatActivity() {
    val fragment = MainVideosFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uzlov_test_layout)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }
}