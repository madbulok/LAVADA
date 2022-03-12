package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.ui.adapters.RecyclerViewScrollListener
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.MainVideosFragmentBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.domain.models.UserFilter
import com.uzlov.dating.lavada.ui.SingleSnap
import com.uzlov.dating.lavada.ui.adapters.PlayerViewAdapter
import com.uzlov.dating.lavada.ui.adapters.ProfileRecyclerAdapter
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentMatch
import com.uzlov.dating.lavada.ui.fragments.profile.ProfileFragment
import com.uzlov.dating.lavada.viemodels.ChatViewModel
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

    private val chatViewModel: ChatViewModel by lazy {
        factoryViewModel.create(ChatViewModel::class.java)
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = factoryViewModel.create(UsersViewModel::class.java)
        model.getUsers()?.observe(this, { users ->
            firebaseEmailAuthService.getUserUid()?.let { it ->
                model.getUser(it)?.observe(this, {
                    it?.url_avatar?.let { it1 -> loadImage(it1, viewBinding.ivProfile) }
                    self = it!!
                    testData = users
                    mAdapter.updateList(
                        model.sortUsers(
                            users, self.lat!!, self.lon!!,
                            userFilter.sex, userFilter.ageStart, userFilter.ageEnd
                        )
                    )
                }
                )
            }
        })

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

                }

                override fun sendHeart(user: User) {
                    self.matches[user.uid] = false
                    val heartFragment = FragmentMatch.newInstance(user)
                    heartFragment.show(childFragmentManager, heartFragment.javaClass.simpleName)
                }

                override fun sendMessage(user: User) {
                    // check VIP
                    firebaseEmailAuthService.getUserUid()?.let { uid->
                        self.chats[user.uid] = self.uid
                        chatViewModel.createChat(uid, user.uid)
                    }

                }
            })
        }
        setOnClickListener()
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
                parentFragmentManager.beginTransaction()
                    .add(R.id.container, chatsFragment)
                    .hide(this@MainVideosFragment)
                    .show(chatsFragment)
                    .addToBackStack(null)
                    .commit()
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
                // TODO: 03.03.2022 нужно на filterLookingForFragment навесить откуда пришел или решить переход как-то иначе
                parentFragmentManager.beginTransaction()
                    .add(R.id.container, filterSearchPeopleFragment)
                    .hide(this@MainVideosFragment)
                    .show(filterSearchPeopleFragment)
                    .addToBackStack(null)
                    .commit()
                PlayerViewAdapter.pauseCurrentPlayingVideo()
  //              Toast.makeText(context, "Меняем фильтры поиска", Toast.LENGTH_SHORT).show()
            }


        }
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
