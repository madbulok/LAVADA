package com.uzlov.dating.lavada.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentSingleChatBinding
import com.uzlov.dating.lavada.domain.models.ChatMessage
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.storage.URIPathHelper
import com.uzlov.dating.lavada.ui.adapters.ChatMessageAdapter
import com.uzlov.dating.lavada.ui.fragments.CompanionInfoFragment
import com.uzlov.dating.lavada.ui.fragments.FilterSearchPeopleFragment
import com.uzlov.dating.lavada.ui.fragments.profile.UploadVideoFragment
import com.uzlov.dating.lavada.viemodels.MessageChatViewModel
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import javax.inject.Inject


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
    private  var selfUid : String = ""
    private lateinit var companionId: String
    private lateinit var companionUID: String
    private lateinit var chatId: String
    private lateinit var listMessage: List<ChatMessage>
    private lateinit var companion : User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = FragmentSingleChatBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // restore intent
        companionId = intent?.getStringExtra(COMPANION_ID) ?: ""
        companionUID = intent?.getStringExtra(COMPANION_UID) ?: ""

        // dagger init
        appComponent.inject(this)

        userViewModel = viewModelFactory.create(UsersViewModel::class.java)
        messageChatViewModel = viewModelFactory.create(MessageChatViewModel::class.java)

        firebaseEmailAuthService.getUser()?.getIdToken(true)?.addOnSuccessListener {
            userViewModel.getUser(it.token.toString())
        }



        messageChatViewModel.chatCreatedData.observe(this) { result ->
            if (result.status == "ok") {
                Log.e("Чат успешно создан, id чата: ", result.data?.chat_id.toString())
                chatId = result.data?.chat_id.toString()
                hideLoading()
            }
        }

        messageChatViewModel.status.observe(this) {
            if (it != null) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                hideLoading()
            }

        }
        messageChatViewModel.listMessageData.observe(this) {
            if (it.isNotEmpty()) {
                messagesAdapter.setMessages(it)
                viewBinding.lvMessages.scrollToPosition((viewBinding.lvMessages.adapter as ChatMessageAdapter).itemCount - 1)
                listMessage = it
                hideLoading()
            }
        }
        messageChatViewModel.messageSendData.observe(this) { result ->
            if (result.status == "ok") {
                firebaseEmailAuthService.getUser()?.getIdToken(true)
                    ?.addOnSuccessListener { tokenFb ->
                        messageChatViewModel.getListMessages(
                            tokenFb.token.toString(),
                            chatId
                        )
                        hideLoading()
                    }
            }
        }
        userViewModel.userByLavadaIdData.observe(this, {
            it?.let { it1 -> updateUiCompanion(it1) }
            companion = it

        })
        viewBinding.btnInfo.setOnClickListener {
          openCompanionInfo(companion)

        }
        initView()
        initChat()
        initCompanion()

    }

    private fun openCompanionInfo(user: User) {
        val fragment = CompanionInfoFragment.newInstance(user)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun initCompanion() {
        firebaseEmailAuthService.getUser()?.getIdToken(true)
            ?.addOnSuccessListener { tokenFb ->
                userViewModel.getUserByLavadaId(tokenFb.token.toString(), companionId)
            }
    }

    private fun initChat() {
        showLoading()
        firebaseEmailAuthService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
            messageChatViewModel.checkChat(tokenFb.token.toString(), companionUID)
            messageChatViewModel.checkChatData.observe(this) { result ->
                if (result.data?.chat_id == null ) {
                   companionId.let {
                        messageChatViewModel.createRemoteChat(tokenFb.token.toString(),
                            it
                        )
                    }
                } else {
                    chatId = result.data.chat_id.toString()
                    messageChatViewModel.getListMessages(tokenFb.token.toString(), chatId)
                    hideLoading()
                }
            }
        }
    }

    private fun showLoading() {
        with(viewBinding) {
            viewBinding.pgContentLoading.visibility = View.VISIBLE
            viewBinding.lvMessages.visibility = View.GONE
            btnSend.isEnabled = false
            textInputLayout.isEnabled = true
        }
    }

    private fun hideLoading() {
        with(viewBinding) {
            viewBinding.pgContentLoading.visibility = View.GONE
            viewBinding.lvMessages.visibility = View.VISIBLE
            btnSend.isEnabled = true
            textInputLayout.isEnabled = true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {

        with(viewBinding) {

            userViewModel.selfUserData.observe(this@SingleChatActivity){ it ->
                selfUid = it.userId.toString()
                messagesAdapter = ChatMessageAdapter(self = selfUid)
                lvMessages.adapter = messagesAdapter
            }
            tbBackAction.setOnClickListener {
                finish()
            }

            btnSend.setOnClickListener {
                if (!chatId.isNullOrEmpty() && viewBinding.textInputLayout.text?.length ?: 0 > 0) {

                    val mes = viewBinding.textInputLayout.text.toString()
//                    chatOpen.let {
//                        it.messages.add(
//                            ChatMessage(
//                                message = viewBinding.textInputLayout.text.toString(),
//                                sender = selfUid,
//                                viewed = false,
//                                mediaUrl = ""
//                            )
//                        )
//                        messageChatViewModel.sendMessage(
//                            uidChat = chatId,
//                            chat = it
//                        )
                    firebaseEmailAuthService.getUser()?.getIdToken(true)
                        ?.addOnSuccessListener { tokenFb ->
                            messageChatViewModel.sendRemoteMessage(
                                tokenFb.token.toString(),
                                chatId,
                                mes
                            )

//                        }
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

            textInputLayout.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    setStateSend(s?.isEmpty() ?: true)
                }
            })
        }

    }

    private fun setStateSend(isGiftState: Boolean) {
        if (isGiftState) {
            viewBinding.btnSend.visibility = View.GONE
            viewBinding.btnGift.visibility = View.VISIBLE
        } else {
            viewBinding.btnSend.visibility = View.VISIBLE
            viewBinding.btnGift.visibility = View.GONE
        }
    }

    private fun loadMessagesHistory() {
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
    }

    private fun updateUiCompanion(user: User) {
        with(viewBinding) {
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
        startActivityForResult(
            Intent.createChooser(intent, "Select image"),
            UploadVideoFragment.REQUEST_CODE
        )
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

    companion object {
        const val CHAT_ID = "chat_id"
        const val COMPANION_ID = "companion_id"
        const val COMPANION_UID = "companion_uid"
    }

}