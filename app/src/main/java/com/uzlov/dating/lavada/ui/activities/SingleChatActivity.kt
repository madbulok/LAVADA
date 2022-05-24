package com.uzlov.dating.lavada.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentSingleChatBinding
import com.uzlov.dating.lavada.domain.models.Chat
import com.uzlov.dating.lavada.domain.models.ChatMessage
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.ui.adapters.ChatMessageAdapter
import com.uzlov.dating.lavada.viemodels.MessageChatViewModel
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import javax.inject.Inject

import android.view.View.OnTouchListener
import com.uzlov.dating.lavada.storage.URIPathHelper
import com.uzlov.dating.lavada.ui.fragments.profile.UploadVideoFragment


class SingleChatActivity : AppCompatActivity() {

    private lateinit var viewBinding: FragmentSingleChatBinding

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var userViewModel: UsersViewModel
    private lateinit var messageChatViewModel: MessageChatViewModel

    private lateinit var messagesAdapter: ChatMessageAdapter

    // data
    private lateinit var selfUid : String
    private lateinit var companionId : String
    private lateinit var chatOpen: Chat
    private lateinit var chatId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = FragmentSingleChatBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // restore intent
        chatId = intent?.getStringExtra(CHAT_ID) ?: ""
        companionId = intent?.getStringExtra(COMPANION_ID) ?: ""

        // dagger init
        appComponent.inject(this)

        userViewModel = viewModelFactory.create(UsersViewModel::class.java)
        messageChatViewModel = viewModelFactory.create(MessageChatViewModel::class.java)

        selfUid = firebaseEmailAuthService.getUserUid() ?: ""
        messagesAdapter = ChatMessageAdapter(self = selfUid)

        initView()
        initChat()
        initCompanion()
        setFullscreen()

    }

    private fun initCompanion() {
            userViewModel.getUser(companionId).observe(this, {
                it?.let { it1 -> updateUiCompanion(it1) }
            })
    }

    private fun initChat() {
        showLoading()
//        messageChatViewModel.createChat(selfUid, companionId)
//            .observe(this, {
//                chatId = it
//                loadMessagesHistory()
//
//            })
    }

    private fun showLoading() {
        with(viewBinding){
            viewBinding.pgContentLoading.visibility = View.VISIBLE
            viewBinding.lvMessages.visibility = View.GONE
            btnSend.isEnabled = false
            textInputLayout.isEnabled = true
        }
    }
    private fun hideLoading() {
        with(viewBinding){
            viewBinding.pgContentLoading.visibility = View.GONE
            viewBinding.lvMessages.visibility = View.VISIBLE
            btnSend.isEnabled = true
            textInputLayout.isEnabled = true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {

        with(viewBinding){
            lvMessages.adapter = messagesAdapter
            tbBackAction.setOnClickListener {
                finish()
            }

            btnSend.setOnClickListener {
                if (!chatId.isNullOrEmpty() && viewBinding.textInputLayout.text?.length ?: 0 > 0) {

                    chatOpen.let {
                        it.messages.add(
                            ChatMessage(
                                message = viewBinding.textInputLayout.text.toString(),
                                sender = selfUid,
                                viewed = false,
                                mediaUrl = ""
                            )
                        )
//                        messageChatViewModel.sendMessage(
//                            uidChat = chatId,
//                            chat = it
//                        )
                    }
                    viewBinding.textInputLayout.setText("")
                }
            }

            btnGift.setOnClickListener {

            }

            btnAttachFile.setOnClickListener {
                openGalleryForVideo()
            }

            viewBinding.textInputLayout.setOnTouchListener(OnTouchListener { v, event ->
//            val DRAWABLE_LEFT = 0
//            val DRAWABLE_TOP = 1
//            val DRAWABLE_RIGHT = 2
//            val DRAWABLE_BOTTOM = 3
//            if (event.action == MotionEvent.ACTION_UP) {
//                if (event.rawX >= viewBinding.etMessage.right - viewBinding.etMessage.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
//                ) {
//                    // your action here
//                    Toast.makeText(this, "open image", Toast.LENGTH_SHORT).show()
//                    return@OnTouchListener true
//                }
//            }
                false
            })

            textInputLayout.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    setStateSend(s?.isEmpty() ?: true)
                }
            })
        }

    }

    private fun setStateSend(isGiftState: Boolean){
        if (isGiftState){
            viewBinding.btnSend.visibility = View.GONE
            viewBinding.btnGift.visibility = View.VISIBLE
        } else {
            viewBinding.btnSend.visibility = View.VISIBLE
            viewBinding.btnGift.visibility = View.GONE
        }
    }

//    private fun loadMessagesHistory() {
//        messageChatViewModel.retrieveMessages(chatId).observe(this, {
//            if (it != null) {
//                chatOpen = it.copy()
//                messagesAdapter.setMessages(it.messages)
//
//            } else {
//                Toast.makeText(this, "Messages is null", Toast.LENGTH_SHORT).show()
//            }
//            hideLoading()
//        })
//    }

    private fun updateUiCompanion(user: User){
        with(viewBinding){
            tvProfileName.text = user.name
            tvSubMessageName.text = user.location
            Glide.with(this@SingleChatActivity)
                .load(user.url_avatar)
                .error(resources.getDrawable(com.uzlov.dating.lavada.R.drawable.test_avatar))
                .into(imageProfile)
        }
    }

    private fun openGalleryForVideo() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_PICK
        }
        startActivityForResult(Intent.createChooser(intent, "Select image"),
            UploadVideoFragment.REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UploadVideoFragment.REQUEST_CODE) {
            if (data?.data != null) {
                val uriPathHelper = URIPathHelper()
                val filePath = data.data?.let { uriPathHelper.getPath(this, it) }

            }
        }
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
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
    }

    companion object {
        const val CHAT_ID = "chat_id"
        const val COMPANION_ID = "companion_id"
    }

}