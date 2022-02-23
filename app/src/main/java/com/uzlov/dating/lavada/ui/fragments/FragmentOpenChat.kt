package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.databinding.FragmentSingleChatBinding
import com.uzlov.dating.lavada.domain.models.Chat
import com.uzlov.dating.lavada.domain.models.ChatMessage
import com.uzlov.dating.lavada.ui.adapters.ChatMessageAdapter
import com.uzlov.dating.lavada.viemodels.MessageViewModel
import javax.inject.Inject

class FragmentOpenChat :
    BaseFragment<FragmentSingleChatBinding>(FragmentSingleChatBinding::inflate) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var messageViewModel: MessageViewModel
    private lateinit var chatId: String
    private var chatOpen: Chat? = null

    private val messagesAdapter by lazy {
        ChatMessageAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        messageViewModel = viewModelFactory.create(MessageViewModel::class.java)
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
        messageViewModel.retrieveMessages(chatId).observe(viewLifecycleOwner, {
            if (it != null) {
                chatOpen = it
                messagesAdapter.setMessages(chatOpen!!.messages ?: emptyList())
            } else {
                Toast.makeText(requireContext(), "response is null", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setOnClickListeners() {
        with(viewBinding) {
            btnSend.setOnClickListener {
                if (!chatId.isNullOrEmpty()) {

                    chatOpen?.let {
                        it.messages?.add(
                            ChatMessage(
                                message = etMessage.text.toString(),
                                sender = "Artem",
                                viewed = false,
                                mediaUrl = "link1"
                            )
                        )
                        messageViewModel.sendMessage(
                            uidChat = chatId,
                            chat = it
                        )
                    }

                    etMessage.setText("")
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