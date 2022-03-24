package com.uzlov.dating.lavada.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.MainVideosFragmentBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.domain.models.UserFilter
import com.uzlov.dating.lavada.ui.SingleSnap
import com.uzlov.dating.lavada.ui.activities.SingleChatActivity
import com.uzlov.dating.lavada.ui.adapters.PlayerViewAdapter
import com.uzlov.dating.lavada.ui.adapters.ProfileRecyclerAdapter
import com.uzlov.dating.lavada.ui.adapters.RecyclerViewScrollListener
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentMatch
import com.uzlov.dating.lavada.ui.fragments.dialogs.GiftsBottomSheetDialogFragment
import com.uzlov.dating.lavada.ui.fragments.profile.ProfileFragment
import com.uzlov.dating.lavada.viemodels.MessageChatViewModel
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import kotlinx.coroutines.delay
import javax.inject.Inject


class MainVideosFragment :
    BaseFragment<MainVideosFragmentBinding>(MainVideosFragmentBinding::inflate) {
    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var factoryViewModel: ViewModelFactory

    private lateinit var model: UsersViewModel

    private val messageChatViewModel: MessageChatViewModel by lazy {
        factoryViewModel.create(MessageChatViewModel::class.java)
    }

    private var self = User()
    private var userFilter = UserFilter()

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
    private var testData = listOf<User>()

    private val mAdapter: ProfileRecyclerAdapter by lazy {
        ProfileRecyclerAdapter(testData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        userFilter = preferenceRepository.readFilter()
        retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = factoryViewModel.create(UsersViewModel::class.java)
        updateData()

        
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
                        delay(500)
                    }
                }
            })
            mAdapter.setOnActionClickListener(object : ProfileRecyclerAdapter.OnActionListener {
                override fun sendGift(user: User) {
                    val giftFragment = GiftsBottomSheetDialogFragment()
                    giftFragment.show(childFragmentManager, giftFragment.javaClass.simpleName)

                }

                override fun sendHeart(user: User) {
                    self.matches[user.uid] = false

                    val heartFragment = FragmentMatch.newInstance(user)
                    heartFragment.show(childFragmentManager, heartFragment.javaClass.simpleName)
                }

                override fun sendMessage(user: User) {
                    // check VIP
                    firebaseEmailAuthService.getUserUid()?.let { selfId ->
                        self.chats[user.uid] = self.uid
                        PlayerViewAdapter.pauseCurrentPlayingVideo()
                        openChatActivity(user.uid)
                    }

                }
            })
        }
        setOnClickListener()
    }

    private fun updateData(){
        model.getUsers()?.observe(this, { users ->
            firebaseEmailAuthService.getUserUid()?.let { it ->
                lifecycleScope.launchWhenResumed {
                    model.getUserSuspend(it)?.let {
                        it.url_avatar?.let { it1 -> loadImage(it1, viewBinding.ivProfile) }
                        self = it
                        testData = users
                        mAdapter.updateList(
                            model.sortUsers(
                                users, self.lat!!, self.lon!!,
                                userFilter.sex, userFilter.ageStart, userFilter.ageEnd, self.black_list)
                        )
                    }
                }
            }
        })
    }
    private val chatsFragment by lazy {
        ChatsFragment()
    }
    private val profileFragment by lazy {
        ProfileFragment()
    }
    private val filterSearchPeopleFragment by lazy {
        FilterSearchPeopleFragment()
    }


    private fun setOnClickListener() {
        with(viewBinding) {
            ivMyMessage.setOnClickListener {
                openChatFragment(null)
                PlayerViewAdapter.pauseCurrentPlayingVideo()
            }
            ivProfile.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .add(R.id.container, profileFragment)
                    .hide(this@MainVideosFragment)
                    .show(profileFragment)
                    .addToBackStack(null)
                    .commit()
                PlayerViewAdapter.pauseCurrentPlayingVideo()
            }
            ivFilter.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .add(R.id.container, filterSearchPeopleFragment)
                    .hide(this@MainVideosFragment)
                    .show(filterSearchPeopleFragment)
                    .addToBackStack(null)
                    .commit()
                PlayerViewAdapter.pauseCurrentPlayingVideo()
            }
            refreshDataLayout.setOnRefreshListener {
                updateData()
                //здесь нужно обновить плеер еще
                refreshDataLayout.isRefreshing = false
            }
        }
    }

    private fun openChatActivity(companionId: String) {
        val intent = Intent(requireContext(), SingleChatActivity::class.java).apply {
            putExtra(SingleChatActivity.COMPANION_ID, companionId)
        }
        startActivity(intent)
    }

    private fun openChatFragment(chatId: String?) {
        val fragment = ChatsFragment.newInstance(companionId = "", chatId = chatId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun loadImage(image: String, container: ImageView) {
        view?.let {
            Glide
                .with(it.context)
                .load(image)
                .error(R.drawable.ic_default_user)
                .into(container)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.e("TAG", "onStop: ")

    }

    override fun onResume() {
        super.onResume()
        PlayerViewAdapter.playCurrentPlayingVideo()
    }

    override fun onPause() {
        super.onPause()
        PlayerViewAdapter.pauseCurrentPlayingVideo()
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
