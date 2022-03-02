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
        User(url_video = "https://firebasestorage.googleapis.com/v0/b/lavada-7777.appspot.com/o/video%2F1646254684222_VID_20220302_233649.mp4?alt=media&token=11dfb4d1-f689-4078-8c7c-238fbca121e1"),
        User(url_video = "https://firebasestorage.googleapis.com/v0/b/lavada-7777.appspot.com/o/video%2F1646254684222_VID_20220302_233649.mp4?alt=media&token=11dfb4d1-f689-4078-8c7c-238fbca121e1"),
        User(url_video = "https://firebasestorage.googleapis.com/v0/b/lavada-7777.appspot.com/o/video%2F1646254684222_VID_20220302_233649.mp4?alt=media&token=11dfb4d1-f689-4078-8c7c-238fbca121e1"),
        User(url_video = "https://firebasestorage.googleapis.com/v0/b/lavada-7777.appspot.com/o/video%2F1646254684222_VID_20220302_233649.mp4?alt=media&token=11dfb4d1-f689-4078-8c7c-238fbca121e1"),
        User(url_video = "https://firebasestorage.googleapis.com/v0/b/lavada-7777.appspot.com/o/video%2F1646254684222_VID_20220302_233649.mp4?alt=media&token=11dfb4d1-f689-4078-8c7c-238fbca121e1"),
        User(url_video = "https://firebasestorage.googleapis.com/v0/b/lavada-7777.appspot.com/o/video%2F1646254684222_VID_20220302_233649.mp4?alt=media&token=11dfb4d1-f689-4078-8c7c-238fbca121e1"),
        User(url_video = "https://firebasestorage.googleapis.com/v0/b/lavada-7777.appspot.com/o/video%2F1646254684222_VID_20220302_233649.mp4?alt=media&token=11dfb4d1-f689-4078-8c7c-238fbca121e1"),
        User(url_video = "https://firebasestorage.googleapis.com/v0/b/lavada-7777.appspot.com/o/video%2F1646254684222_VID_20220302_233649.mp4?alt=media&token=11dfb4d1-f689-4078-8c7c-238fbca121e1"),
        User(url_video = "https://firebasestorage.googleapis.com/v0/b/lavada-7777.appspot.com/o/video%2F1646254684222_VID_20220302_233649.mp4?alt=media&token=11dfb4d1-f689-4078-8c7c-238fbca121e1"),
        User(url_video = "https://firebasestorage.googleapis.com/v0/b/lavada-7777.appspot.com/o/video%2F1646254684222_VID_20220302_233649.mp4?alt=media&token=11dfb4d1-f689-4078-8c7c-238fbca121e1"),
        User(url_video = "https://firebasestorage.googleapis.com/v0/b/lavada-7777.appspot.com/o/video%2F1646254684222_VID_20220302_233649.mp4?alt=media&token=11dfb4d1-f689-4078-8c7c-238fbca121e1"),
        User(url_video = "https://firebasestorage.googleapis.com/v0/b/lavada-7777.appspot.com/o/video%2F1646254684222_VID_20220302_233649.mp4?alt=media&token=11dfb4d1-f689-4078-8c7c-238fbca121e1"),
        User(url_video = "https://firebasestorage.googleapis.com/v0/b/lavada-7777.appspot.com/o/video%2F1646254684222_VID_20220302_233649.mp4?alt=media&token=11dfb4d1-f689-4078-8c7c-238fbca121e1"),
    )
    private val mAdapter: ProfileRecyclerAdapter by lazy {
        ProfileRecyclerAdapter(testData)
    }

    private val self = User() // TODO(Inject current user from dagger)

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
                override fun sendGift(user: User) {

                }

                override fun sendHeart(user: User) {
                    self.matches[user.uid] = false
                    val heartFragment = FragmentMatch()
                    heartFragment.show(childFragmentManager, heartFragment.javaClass.simpleName)
                }

                override fun sendMessage(user: User) {
                    // check VIP
                    self.chats[user.uid] = self.uid
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
