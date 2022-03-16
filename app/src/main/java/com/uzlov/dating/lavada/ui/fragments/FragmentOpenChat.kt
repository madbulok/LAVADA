package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
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

class FragmentOpenChat :
    BaseFragment<FragmentSingleChatBinding>(FragmentSingleChatBinding::inflate) {

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var userViewModel: UsersViewModel
    private lateinit var messageChatViewModel: MessageChatViewModel

    private lateinit var chatId: String
    private var chatOpen: Chat? = null
    private val selfUid by lazy {
        firebaseEmailAuthService.getUserUid() ?: ""
    }

    private val messagesAdapter by lazy {
        ChatMessageAdapter(self = "Artem2")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        userViewModel = viewModelFactory.create(UsersViewModel::class.java)
        messageChatViewModel = viewModelFactory.create(MessageChatViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        viewBinding.lvMessages.adapter = messagesAdapter
        arguments?.let {
            chatId = it.getString(CHAT_ID) ?: ""
            viewBinding.tvProfileName.text = it.getString(CHAT_ID) ?: "Error chat!"
        }

        loadMessagesHistory()
    }


    private fun loadMessagesHistory() {
        messageChatViewModel.retrieveMessages(chatId).observe(viewLifecycleOwner, {
            if (it != null) {
                chatOpen = it.copy()
                messagesAdapter.setMessages(it.messages ?: emptyList())

//                it.members?.firstOrNull { it != selfUid }?.let { uidCompanion->
//                    userViewModel.getUser(uidCompanion)?.observe(viewLifecycleOwner, { user->
//                        user?.let { it1 -> updateUiCompanion(it1) }
//                    })
//                }
            } else {
                Toast.makeText(requireContext(), "response is null", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUiCompanion(user: User){
        with(viewBinding){
            tvProfileName.text = user.name
            tvSubMessageName.text = user.location
            Glide.with(requireContext())
                .load(user.url_avatar)
                .error(resources.getDrawable(R.drawable.test_avatar))
                .into(imageProfile)
        }
    }

    private fun setOnClickListeners() {
        with(viewBinding) {
            btnSend.setOnClickListener {
                if (!chatId.isNullOrEmpty() && textInputLayout.text?.length ?: 0 > 0) {

                    chatOpen?.let {
                        it.messages?.add(
                            ChatMessage(
                                message = textInputLayout.text.toString(),
                                sender = selfUid,
                                viewed = false,
                                mediaUrl = "link1"
                            )
                        )
                        messageChatViewModel.sendMessage(
                            uidChat = chatId,
                            chat = it
                        )
                    }
                    textInputLayout.setText("")
                }
            }

            tbBackAction.setOnClickListener {
                parentFragmentManager.popBackStack()

            }

            btnInfo.setOnClickListener {

            }
        }
    }

    companion object {
        const val CHAT_ID: String = "chat_id"
        fun newInstance(chatId: String): FragmentOpenChat {
            val fragment = FragmentOpenChat()
            fragment.arguments = bundleOf(FragmentOpenChat.CHAT_ID to chatId)
            return fragment
        }
    }

}