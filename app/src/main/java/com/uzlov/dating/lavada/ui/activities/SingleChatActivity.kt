package com.uzlov.dating.lavada.ui.activities

import android.os.Bundle
import android.view.View
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

    }

    private fun initCompanion() {
        lifecycleScope.launchWhenResumed {
            val user = userViewModel.getUser(companionId)
            if (user == null){
                Toast.makeText(this@SingleChatActivity, "User null", Toast.LENGTH_SHORT).show()
            } else {
                updateUiCompanion(user)
            }
        }
    }

    private fun initChat() {
        showLoading()
        messageChatViewModel.createChat(selfUid, companionId)
            .observe(this, {
                chatId = it
                loadMessagesHistory()

            })
    }

    private fun showLoading() {
        with(viewBinding){
            viewBinding.pgContentLoading.visibility = View.VISIBLE
            viewBinding.lvMessages.visibility = View.GONE
            btnSend.isEnabled = false
            etMessage.isEnabled = true
        }
    }
    private fun hideLoading() {
        with(viewBinding){
            viewBinding.pgContentLoading.visibility = View.GONE
            viewBinding.lvMessages.visibility = View.VISIBLE
            btnSend.isEnabled = true
            etMessage.isEnabled = true
        }
    }

    private fun initView() {
        viewBinding.lvMessages.adapter = messagesAdapter
        viewBinding.tbBackAction.setOnClickListener {
            finish()
        }

        viewBinding.btnSend.setOnClickListener {
            if (!chatId.isNullOrEmpty() && viewBinding.etMessage.text?.length ?: 0 > 0) {

                chatOpen.let {
                    it.messages?.add(
                        ChatMessage(
                            message = viewBinding.etMessage.text.toString(),
                            sender = selfUid,
                            viewed = false,
                            mediaUrl = ""
                        )
                    )
                    messageChatViewModel.sendMessage(
                        uidChat = chatId,
                        chat = it
                    )
                }
                viewBinding.etMessage.setText("")
            }
        }
    }

    private fun loadMessagesHistory() {
        messageChatViewModel.retrieveMessages(chatId).observe(this, {
            if (it != null) {
                chatOpen = it.copy()
                messagesAdapter.setMessages(it.messages ?: emptyList())

            } else {
                Toast.makeText(this, "Messages is null", Toast.LENGTH_SHORT).show()
            }
            hideLoading()
        })
    }

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

    companion object {
        const val CHAT_ID = "chat_id"
        const val COMPANION_ID = "companion_id"
    }

}