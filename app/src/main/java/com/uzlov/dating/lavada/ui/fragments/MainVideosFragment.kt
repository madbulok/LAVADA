package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.ui.adapters.RecyclerViewScrollListener
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.databinding.MainVideosFragmentBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.ui.SingleSnap
import com.uzlov.dating.lavada.ui.adapters.PlayerViewAdapter
import com.uzlov.dating.lavada.ui.adapters.ProfileRecyclerAdapter
import kotlinx.coroutines.delay

class MainVideosFragment :
    BaseFragment<MainVideosFragmentBinding>(MainVideosFragmentBinding::inflate) {

    private val scrollListener: RecyclerViewScrollListener by lazy {
        object : RecyclerViewScrollListener() {
            override fun onItemIsFirstVisibleItem(index: Int) {
                if (index != -1) PlayerViewAdapter.playIndexThenPausePreviousPlayer(index)
            }
        }
    }

    private val callback = object : SingleSnap.OnScrollListener {
        override fun onNext() {
        }

        override fun onPrevious() {
        }
    }

    private val snapHelper: SingleSnap = SingleSnap(callback)

    private val testData = listOf<User>(
        User(url_video = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
        User(url_video = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"),
        User(url_video = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"),
        User(url_video = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"),
        User(url_video = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"),
        User(url_video = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"),
        User(url_video = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4"),
        User(url_video = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"),
        User(url_video = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4"),
        User(url_video = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"),
        User(url_video = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4"),
        User(url_video = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4"),
        User(url_video = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4"),
    )
    private val mAdapter: ProfileRecyclerAdapter by lazy {
        ProfileRecyclerAdapter(testData)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding) {
            rvVideosUsers.adapter = mAdapter
            rvVideosUsers.setHasFixedSize(true)
            rvVideosUsers.addOnScrollListener(scrollListener)
            snapHelper.attachToRecyclerView(rvVideosUsers)

            // double click send love
            mAdapter.setOnItemClickListener(object : ProfileRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, model: User?) {
                    Toast.makeText(requireContext(), "Вы отправили симпатию", Toast.LENGTH_SHORT)
                        .show()
                    lifecycleScope.launchWhenResumed {
                        with(viewBinding) {
                            delay(500)
                        }
                    }
                }
            })

            mAdapter.setOnActionClickListener(object : ProfileRecyclerAdapter.OnActionListener {
                override fun sendGift() {

                }

                override fun sendHeart() {
                    val heartFragment = FragmentMatch()
                    heartFragment.show(childFragmentManager, heartFragment.javaClass.simpleName)
                }

                override fun sendMessage() {

                }
            })
        }

        mAdapter.updateList(testData)

        setOnClickListener()
    }

    private val chatsFragment by lazy {
        ChatsFragment()
    }

    private fun setOnClickListener() {
        with(viewBinding) {
            ivMyMessage.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .add(R.id.container, chatsFragment)
                    .hide(this@MainVideosFragment)
                    .show(chatsFragment)
                    .addToBackStack(null)
                    .commit()
                PlayerViewAdapter.pauseCurrentPlayingVideo()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PlayerViewAdapter.releaseAllPlayers()
    }

    companion object {

        private const val CURRENT_USER = "user"
        fun newInstance() =
            MainVideosFragment().apply {
                arguments = Bundle().apply {
                }
            }

        fun newInstance(userId: String) =
            MainVideosFragment().apply {
                arguments = Bundle().apply {
                    putString(CURRENT_USER, userId)
                }
            }
    }
}
