package com.uzlov.dating.lavada.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.HostActivityBinding
import com.uzlov.dating.lavada.ui.adapters.PlayerViewAdapter
import com.uzlov.dating.lavada.ui.fragments.MainVideosFragment

class HostActivity : AppCompatActivity() {
    private var _viewBinding: HostActivityBinding? = null
    private val viewBinding get() = _viewBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewBinding = HostActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        openFragment()
    }

    fun openFragment(){
        supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.container, MainVideosFragment())
                .commit()
        }
    }
    fun rollbackFragment() = supportFragmentManager.popBackStack()

    override fun onStop() {
        super.onStop()
        PlayerViewAdapter.pauseAllPlayers()
    }
}