package com.uzlov.dating.lavada.ui.activities

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.HostActivityBinding
import com.uzlov.dating.lavada.ui.adapters.PlayerViewAdapter
import com.uzlov.dating.lavada.ui.fragments.MainVideosFragment
import com.uzlov.dating.lavada.ui.fragments.ShopFragment
import com.uzlov.dating.lavada.ui.fragments.profile.PreviewVideoFragment
import javax.inject.Inject

class HostActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    private var _viewBinding: HostActivityBinding? = null
    private val viewBinding get() = _viewBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewBinding = HostActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        if (savedInstanceState == null) {
            openFragment()
        }
        /* вот эта хрень делает статусбар прозрачным. Но мне ваще не нравится оно(
        пока поставила фулскрин
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false) */
        setFullscreen()
    }
    /*
    private fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }*/

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

    fun openFragment() {
        supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.container, MainVideosFragment())
                .commit()
        }
    }

    fun rollbackFragment() = supportFragmentManager.popBackStack()

    fun startShopFragment() {
        supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.container, ShopFragment.newInstance())
                .addToBackStack("null")
                .commit()
        }
    }

    fun showPreviewVideo(_path: String, request: Int) {
        supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.container, PreviewVideoFragment.newInstance(_path, request))
                .addToBackStack("null")
                .commit()
        }
    }

    override fun onStop() {
        super.onStop()
        PlayerViewAdapter.pauseAllPlayers()
        Log.e("TAG A", "onStop: ")
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.e("TAG A", "onDestroy: ")
    }
}