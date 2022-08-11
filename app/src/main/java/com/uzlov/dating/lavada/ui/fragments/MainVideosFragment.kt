package com.uzlov.dating.lavada.ui.fragments

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
import com.uzlov.dating.lavada.ui.fragments.profile.ProfileFragment
import com.uzlov.dating.lavada.viemodels.SubscriptionsViewModel
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

    private val subsViewModel: SubscriptionsViewModel by lazy {
        factoryViewModel.create(SubscriptionsViewModel::class.java)
    }

    private var self = User()
    private var userFilter = UserFilter()
    private var userMes = User()

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
        ProfileRecyclerAdapter(testData, self)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        userFilter = preferenceRepository.readFilter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = factoryViewModel.create(UsersViewModel::class.java)
        initObservers()
        updateData()
        with(viewBinding) {
            rvVideosUsers.adapter = mAdapter
            rvVideosUsers.setHasFixedSize(true)
            rvVideosUsers.addOnScrollListener(scrollListener)
            snapHelper.attachToRecyclerView(rvVideosUsers)

            // double click send love
            mAdapter.setOnItemClickListener(object : ProfileRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, user: User?) {
                    /**
                     * тут, же дублируется отправка сердечка просто?
                     * - отправляем лайк,
                     * - проверяем его на взаимность,
                     * - если взаимен, открываем фрагмент matches;
                     * - если нет - тост "вы отправили симпатию"*/
                    if (user != null) {
                        authService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
                            model.setLike(tokenFb.token ?: "", user.uid, "1")
                            Log.e("javaClass.simpleName", "sendHeart: ")
                        }
                    }

                }
            })

        }
        setOnClickListener()

        subsViewModel.getAvailableSubscriptions("")
    }

    private fun initObservers(){
        //лайки
        model.selfUserData.observe(viewLifecycleOwner){ result ->
            self = result
        }
        model.likes.observe(viewLifecycleOwner) {
            Log.e("javaClass.simpleName", "likes recieve")
            it.let { user ->
                if (!user.matches.getValue(user.uid)) {
                    Toast.makeText(
                        requireContext(),
                        "Вы отправили лайк пользователю",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (user.matches.getValue(user.uid)) {
                    self.matches[it.uid] = false
                    val heartFragment = FragmentMatch.newInstance(it)
                    heartFragment.show(
                        childFragmentManager,
                        heartFragment.javaClass.simpleName
                    )
                }
            }
        }
        //пользователи
        model.listUsersData.observe(viewLifecycleOwner) { users ->
            model.selfUserData.observe(viewLifecycleOwner) { user ->
                Log.e("TAG", "updateData: $user")
                user?.url_avatar?.let { it -> loadImage(it, viewBinding.ivProfile) }
                self = user?.copy()!!
                testData = users

            }
            /** пока без сортировки*/
            mAdapter.updateList(
                users, self
            )
        }
        //статус доступа к api
        model.status.observe(viewLifecycleOwner) { result ->
            if (!result.isNullOrEmpty()) {
                Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show()
                model.status.value = null
            }
        }
        model.checkedLikeData.observe(viewLifecycleOwner){ result ->
            if (result.data?._mutual_like == "1"){
                Log.e("ВЗАИМНО, МОЖНО ОТКРЫВАТЬ ЧАТ", result.data._mutual_like.toString())
                PlayerViewAdapter.pauseAllPlayers()
                userMes.userId?.let { openChatActivity(it, userMes.uid) }
            } else {
                showCustomAlertOnlyPremium()
            }

        }
    }

    private fun openChatActivity(companionId: String, companionUid: String) {
        val intent = Intent(requireContext(), SingleChatActivity::class.java).apply {
            putExtra(SingleChatActivity.COMPANION_ID, companionId)
            putExtra(SingleChatActivity.COMPANION_UID, companionUid)
        }
        startActivity(intent)
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
        val btDismiss = dialogView.findViewById<TextView>(R.id.btDismissCustomDialog)
        btDismiss.setOnClickListener {
            customDialog?.dismiss()
        }
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
        authService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
            Log.e("TOKEN_FB", tokenFb.token.toString())
            model.getUsers(tokenFb.token.toString(), preferenceRepository.readFilter().sex, preferenceRepository.readFilter().ageStart.toString(), preferenceRepository.readFilter().ageEnd.toString())
            model.getUser(tokenFb.token.toString())
        }

    }

    private val profileFragment by lazy {
        ProfileFragment()
    }


    private fun setOnClickListener() {
        with(viewBinding) {
            ivMyMessage.setOnClickListener {
//                openChatFragment(null)
//                PlayerViewAdapter.pauseCurrentPlayingVideo()
                showCustomAlertComingSoon()
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
                openFilterSearch(self)
                PlayerViewAdapter.pauseCurrentPlayingVideo()
            }
            refreshDataLayout.setOnRefreshListener {
                //здесь нужно занулить плееры
                PlayerViewAdapter.releaseAllPlayers()
                authService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
                    model.getUsers(tokenFb.token.toString(), preferenceRepository.readFilter().sex, preferenceRepository.readFilter().ageStart.toString(), preferenceRepository.readFilter().ageEnd.toString())
                    Log.e("javaClass.simpleName", "listUsers: ")
                }
                // а тут - запустить их заново

                refreshDataLayout.isRefreshing = false
            }
        }

        mAdapter.setOnActionClickListener(object : ProfileRecyclerAdapter.OnActionListener {
            override fun sendGift(user: User) {
//                val giftFragment = GiftsBottomSheetDialogFragment()
//                giftFragment.show(childFragmentManager, giftFragment.javaClass.simpleName)
                showCustomAlertComingSoon()

            }

            override fun sendHeart(user: User) {
                /**
                 * тут, я так понимаю, будет так
                 * - отправляем лайк,
                 * - проверяем его на взаимность,
                 * - если взаимен, открываем фрагмент matches;
                 * - если нет - тост "вы отправили симпатию"*/
                authService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
                    model.setLike(tokenFb.token ?: "", user.uid, "1")
                    Log.e("javaClass.simpleName", "sendHeart: ")
                }


            }

            override fun sendMessage(user: User) {
//                userMes = user
//                //check likes?
//                /**
//                 * тут будет так
//                 * - проверяем лайк,
//                 * - если взаимен, открываем сообщения
//                 * - если нет, проверяем премиум
//                 * - если премиум, то открываем сообщения, если нет, открываем алерт с прежложением купить премиум*/
//                if (self.premium) {
////                    self.chats[user.uid] = self.uid
//                    PlayerViewAdapter.pauseCurrentPlayingVideo()
//                    user.userId?.let { it1 -> openChatActivity(it1, user.uid)
//                    }
//                } else {
//                    authService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
//                        model.checkLike(tokenFb.token ?: "", user.uid)
//                        Log.e("javaClass.simpleName", "checkLike: ")
//                    }
//                }
                showCustomAlertComingSoon()

            }

            override fun complain(user: User) {
                showCustomAlertToComplain()
            }

            override fun tiktok(user: User) {
                showCustomAlertComingSoon()
            }

            override fun instagram(user: User) {
                showCustomAlertComingSoon()
            }

            override fun facebook(user: User) {
                showCustomAlertComingSoon()
            }
        })
    }



    private fun openFilterSearch(user: User) {
        val fragment = FilterSearchPeopleFragment.newInstance(user)
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
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
                .placeholder(R.drawable.ic_default_user)
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

    override fun onStart() {
        super.onStart()
        PlayerViewAdapter.playCurrentPlayingVideo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PlayerViewAdapter.releaseAllPlayers()
    }

    private fun showCustomAlertComingSoon() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_coming_soon, null)
        val customDialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_rounded)
                .setView(dialogView)
                .show()

        dialogView.findViewById<TextView>(R.id.header).text =
            getString(R.string.ficha_is_coming_soon)
        val btSendPass = dialogView.findViewById<Button>(R.id.btnSendPasswordCustomDialog)
        btSendPass.text = getString(R.string.i_ll_be_waiting)
        btSendPass.setOnClickListener {
            customDialog?.dismiss()
        }
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

