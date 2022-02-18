package com.uzlov.dating.lavada.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.ui.fragments.MainVideosFragment
import com.uzlov.dating.lavada.ui.fragments.profile.ProfileFragment
import com.uzlov.dating.lavada.ui.fragments.profile.UploadVideoFragment

class HostActivity : AppCompatActivity() {

    private var bottomNavigation: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.host_activity)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation?.itemIconTintList = null
        setFragment(MainVideosFragment.newInstance())
        bottomNavigation?.inflateMenu(R.menu.main_menu)

        bottomNavigation?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.upload_video -> {
                        setFragment(UploadVideoFragment.newInstance())
                        return@setOnItemSelectedListener true
                }
                R.id.create_video -> {
                    //поменять на нужное
                    setFragment(ProfileFragment.newInstance())
                    return@setOnItemSelectedListener true
                }
                R.id.main_action -> {
                        setFragment(MainVideosFragment.newInstance())
                        return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}