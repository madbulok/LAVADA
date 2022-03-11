package com.uzlov.dating.lavada.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.HostActivityBinding
import com.uzlov.dating.lavada.ui.adapters.PlayerViewAdapter
import com.uzlov.dating.lavada.ui.fragments.MainVideosFragment
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
        if (savedInstanceState == null){
            openFragment()
        }
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