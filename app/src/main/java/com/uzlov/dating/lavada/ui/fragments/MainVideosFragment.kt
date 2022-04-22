package com.uzlov.dating.lavada.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.MainVideosFragmentBinding
import com.uzlov.dating.lavada.domain.models.*
import com.uzlov.dating.lavada.retrofit.RemoteDataSource
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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

        //получаем токен от fb
        val userToken = firebaseEmailAuthService.auth.currentUser?.getIdToken(true)
        userToken?.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val idToken: String? = task.result?.token
                Log.d("TOKEN!!!!", idToken!!)
                lifecycleScope.launchWhenResumed {
                    //      получаем список тут
                    try {
                        val params = HashMap<String?, String?>()
                        params["token"] = idToken
                        val remote = model.authRemoteUser(params)
                        if (remote != null) {
//                            val map = HashMap<String, RequestBody>()
//                            map["user_description"] = "Интересная блондинка".toRequestBody("text/plain".toMediaTypeOrNull())
//                            map["user_email"] = "testuser1@test.com".toRequestBody()
//                            map["user_age"] = "18".toRequestBody()
//                            map["user_location_lng"] = "37.555555".toRequestBody()
//                            map["user_location_lat"] = "55.555555".toRequestBody()
//                            map["user_address"] = "Москва".toRequestBody()
//                            //    map["user_gender"] = "FEMALE"
//                            map["user_nickname"] = "Кристина".toRequestBody()
                            model.getRemoteUser(remote)
                            //              model.updateRemoteUser(remote!!, map).toString()

                        }

//                        val balance = HashMap<String, String>()
//                        balance["expiration_pay"] = "2022-06-10 00:00:00"
//                        balance["pay_system"] = "google"
//                        balance["trx_id"] = "452fk sgio"
//                        balance["amount"] = "300"
//                        balance["meta[0]"] = JsonArray().toString()
//                        balance["status"] = "succeeded"
                        //           Log.d("GET_REMOTE_USER_BY_ID", model.getRemoteUserById(remote.data.token!!, "yX6MhMaHZzb7APFKyeQNZpAvmfk2").toString())
                        //        Log.d("POST_BALANCE",
                        //            model.postSubscribe(remote.data.token!!, balance).toString()
                        //       )
                        //      Log.d("NEW_BALANCE", model.getRemoteBalance(remote.data.token!!).toString())
                    } catch (e: Exception) {
                        Log.e("EXCEPTION", e.toString())
                    }
                }
            } else {
                // Handle error -> task.getException();
            }
        }


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
                    //check likes?
                    if (self.premium == true) {

                        firebaseEmailAuthService.getUserUid()?.let { selfId ->
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
            context?.let {
                MaterialAlertDialogBuilder(it, R.style.MaterialAlertDialog_rounded)
                    .setView(dialogView)
                    .show()
            }
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
            Toast.makeText(context, "Жалуемся", Toast.LENGTH_SHORT).show()
            customDialog?.dismiss()
        }
    }

    private fun showCustomAlertOnlyPremium() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_layout, null)
        val customDialog = context?.let {
            MaterialAlertDialogBuilder(it, R.style.MaterialAlertDialog_rounded)
                .setView(dialogView)
                .show()
        }
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
        model.getUsers()?.observe(this, { users ->
            firebaseEmailAuthService.getUserUid()?.let { it ->
                lifecycleScope.launchWhenResumed {
                    model.getUserSuspend(it)?.let {
                        it.url_avatar?.let { it1 -> loadImage(it1, viewBinding.ivProfile) }
                        self = it
                        testData = users
                        mAdapter.updateList(
                            model.sortUsers(
                                users,
                                self.lat!!,
                                self.lon!!,
                                userFilter.sex,
                                userFilter.ageStart,
                                userFilter.ageEnd,
                                self.black_list
                            )
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

