package com.uzlov.dating.lavada.ui.activities

import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.video.OutputResults
import androidx.fragment.app.FragmentManager
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.HostActivityBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.service.BootLoaderService
import com.uzlov.dating.lavada.service.MatchesService
import com.uzlov.dating.lavada.service.NewMessageService
import com.uzlov.dating.lavada.ui.adapters.PlayerViewAdapter
import com.uzlov.dating.lavada.ui.fragments.MainVideosFragment
import com.uzlov.dating.lavada.ui.fragments.ShopFragment
import com.uzlov.dating.lavada.ui.fragments.VideoCaptureFragment
import com.uzlov.dating.lavada.ui.fragments.profile.PreviewVideoFragment
import javax.inject.Inject


class HostActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    private var _viewBinding: HostActivityBinding? = null
    private val viewBinding get() = _viewBinding!!

    private val br = BootLoaderService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewBinding = HostActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        if (savedInstanceState == null) {
            openFragment()
        }
  //      setFullscreen()
        startService(Intent(baseContext, MatchesService::class.java))
        startService(Intent(baseContext, NewMessageService::class.java))
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BOOT_COMPLETED)
        this.registerReceiver(br, filter)
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
        } else
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }

    fun openFragment() {
        supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.container, MainVideosFragment())
                .commit()
        }
    }

    fun rollbackFragment() = supportFragmentManager.popBackStack()

    fun rollbackFragmentWithFlag() = supportFragmentManager.popBackStack("uploadVideo",
        FragmentManager.POP_BACK_STACK_INCLUSIVE
    )

    fun startShopFragment() {
        supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.container, ShopFragment.newInstance())
                .addToBackStack("null")
                .commit()
        }
    }

    fun showPreviewVideo(_path: String, request: Int, user: User) {
        supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.container, PreviewVideoFragment.newInstance(_path, request, user))
                .addToBackStack("uploadVideo")
                .commit()
        }
    }

    fun startCaptureVideoFragment(request: Int, user: User) {

        val videoCaptureCallback =
            object : VideoCaptureFragment.VideoRecordingListener {
                override fun start() {
                    // need disable all transactions
                }

                override fun finish(result: OutputResults) {
                    val realPath = getRealPathFromURI(this@HostActivity, result.outputUri)
                    if (realPath != null) {
                        showPreviewVideo(realPath, request, user)
                    }
                }

                override fun error(message: String) {
                    Toast.makeText(this@HostActivity, message, Toast.LENGTH_SHORT).show()
                }
            }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, VideoCaptureFragment(videoCaptureCallback))
            .addToBackStack(("uploadVideo"))
            .commit()
    }

    fun getRealPathFromURI(context: android.content.Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = contentUri?.let { context.contentResolver.query(it, proj, null, null, null) }
            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            cursor?.close()
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