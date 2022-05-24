package com.uzlov.dating.lavada.ui.fragments

import GiftsViewModels
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.MainVideosFragmentBinding
import com.uzlov.dating.lavada.domain.models.*
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
import javax.inject.Inject


class MainVideosFragment :
    BaseFragment<MainVideosFragmentBinding>(MainVideosFragmentBinding::inflate) {

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    @Inject
    lateinit var authService: FirebaseEmailAuthService

    @Inject
    lateinit var factoryViewModel: ViewModelFactory

    private lateinit var model: UsersViewModel

    private val messageChatViewModel: MessageChatViewModel by lazy {
        factoryViewModel.create(MessageChatViewModel::class.java)
    }

    private val giftsViewModels: GiftsViewModels by lazy {
        factoryViewModel.create(GiftsViewModels::class.java)
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
        updateData()
        with(viewBinding) {
            rvVideosUsers.adapter = mAdapter
            rvVideosUsers.setHasFixedSize(true)
            rvVideosUsers.addOnScrollListener(scrollListener)
            snapHelper.attachToRecyclerView(rvVideosUsers)

            // double click send love
            mAdapter.setOnItemClickListener(object : ProfileRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, model: User?) {
                    /**
                     * тут, же дублируется отправка сердечка просто?
                     * - отправляем лайк,
                     * - проверяем его на взаимность,
                     * - если взаимен, открываем фрагмент matches;
                     * - если нет - тост "вы отправили симпатию"*/
                    Toast.makeText(requireContext(), "Вы отправили симпатию", Toast.LENGTH_SHORT)
                        .show()
                }
            })
            mAdapter.setOnActionClickListener(object : ProfileRecyclerAdapter.OnActionListener {
                override fun sendGift(user: User) {
                    val giftFragment = GiftsBottomSheetDialogFragment()
                    giftFragment.show(childFragmentManager, giftFragment.javaClass.simpleName)

                }

                override fun sendHeart(user: User) {
                    /**
                     * тут, я так понимаю, будет так
                     * - отправляем лайк,
                     * - проверяем его на взаимность,
                     * - если взаимен, открываем фрагмент matches;
                     * - если нет - тост "вы отправили симпатию"*/
                    self.matches[user.uid] = false
                    val heartFragment = FragmentMatch.newInstance(user)
                    heartFragment.show(childFragmentManager, heartFragment.javaClass.simpleName)
                }

                override fun sendMessage(user: User) {
                    //check likes?
                    /**
                     * тут будет так
                     * - проверяем лайк,
                     * - если взаимен, открываем сообщения
                     * - если нет, проверяем премиум
                     * - если премиум, то открываем сообщения, если нет, открываем алерт с прежложением купить премиум*/
                    if (self.premium) {
                        authService.getUserUid()?.let {
                            self.chats[user.uid] = self.uid
                            PlayerViewAdapter.pauseCurrentPlayingVideo()
                            openChatActivity(user.uid)
                        }
                    } else {
                        showCustomAlertOnlyPremium()
                    }

                }

                override fun complain(user: User) {
                    showCustomAlertToComplain()
                }
            })
        }
        setOnClickListener()
    }


    private fun showCustomAlertToComplain() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_layout, null)
        val customDialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_rounded)
                .setView(dialogView)
                .show()

        val btDismiss = dialogView.findViewById<TextView>(R.id.btDismissCustomDialog)
        btDismiss.text = getString(R.string.no)
        btDismiss.setOnClickListener {
            customDialog?.dismiss()
        }
        dialogView.findViewById<TextView>(R.id.header).text =
            getString(R.string.this_video_contains_inappropriate_content)
        dialogView.findViewById<TextView>(R.id.description).visibility = View.INVISIBLE
        val btSendPass = dialogView.findViewById<Button>(R.id.btnSendPasswordCustomDialog)
        btSendPass.text = getString(R.string.yes)
        btSendPass.setOnClickListener {
            Toast.makeText(requireContext(), "Жалуемся", Toast.LENGTH_SHORT).show()
            customDialog?.dismiss()
        }
    }

    private fun showCustomAlertOnlyPremium() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_layout, null)
        val customDialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_rounded)
                .setView(dialogView)
                .show()

        dialogView.findViewById<TextView>(R.id.description).text =
            getString(R.string.go_to_the_store_to_buy_premium)

        dialogView.findViewById<TextView>(R.id.header).text =
            getString(R.string.only_available_to_premium_accounts)

        val btSendPass = dialogView.findViewById<Button>(R.id.btnSendPasswordCustomDialog)
        btSendPass.text = getString(R.string.go_to_shop)
        btSendPass.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, ShopFragment())
                .addToBackStack(null)
                .commit()
            customDialog?.dismiss()

        }
    }

    private fun updateData() {
        // Работает.
        // 1) Получаем token firebase
        // 2) Отправляем token firebase на наш сервер для авороизации
        // 3) Получаем от нашего сервера token для общения с ним (нашим сервером)
        authService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
            Log.e("TOKEN_FB", tokenFb.token.toString())
            model.authRemoteUser(hashMapOf("token" to tokenFb.token))
                .observe(viewLifecycleOwner) { tokenBack ->
                    model.getUsers(tokenBack).observe(this, { users ->
                        Log.e("MV_TOKEN_BACK", tokenBack)
                        model.getUser(tokenBack).observe(viewLifecycleOwner) { user ->
                            Log.e("TAG", "updateData: $user")
                            user?.url_avatar?.let { it -> loadImage(it, viewBinding.ivProfile) }
                            self = user?.copy()!!
                            testData = users
                            Log.e("USERS", users.toString())

                            /** пока без сортировки*/
                            mAdapter.updateList(
                                users
                            )
                        }
                    })
                }
        }

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
        fun newInstance() = MainVideosFragment()

        fun newInstance(userId: String) =
            MainVideosFragment().apply {
                arguments = Bundle().apply {
                    putString(CURRENT_USER, userId)
                }
            }
    }
}

