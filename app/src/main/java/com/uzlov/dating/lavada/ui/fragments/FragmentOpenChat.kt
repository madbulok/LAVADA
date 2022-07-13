package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
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
import kotlin.math.roundToInt

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
    private val selfUid = ""
    private lateinit var companion : User
    private lateinit var listMessage: List<ChatMessage>

    private lateinit var messagesAdapter: ChatMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        userViewModel = viewModelFactory.create(UsersViewModel::class.java)
        messageChatViewModel = viewModelFactory.create(MessageChatViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()

        messagesAdapter = ChatMessageAdapter(self = firebaseEmailAuthService.getUserUid() ?: "")
        viewBinding.lvMessages.adapter = messagesAdapter
        arguments?.let {
            chatId = it.getString(CHAT_ID) ?: ""
            viewBinding.tvProfileName.text = it.getString(CHAT_ID) ?: "Error chat!"
        }

        initChat()
        userViewModel.userByLavadaIdData.observe(this, {
            it?.let { it1 -> updateUiCompanion(it1) }
            companion = it

        })
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
            } else {
                Log.e("Все пошло не по плану", result.status.toString())
                hideLoading()
            }
        }
        messageChatViewModel.companionData.observe(this, { companion ->
            initCompanion(companion)

        })
        userViewModel.userByLavadaIdData.observe(this, {
            it?.let { it1 -> updateUiCompanion(it1) }
        })

    }

    private fun initChat() {
        showLoading()
        firebaseEmailAuthService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
            messageChatViewModel.getChatById(tokenFb.token.toString(), chatId)
            messageChatViewModel.getListMessages(tokenFb.token.toString(), chatId)
        }
    }

    private fun initCompanion(companionId: String) {
        firebaseEmailAuthService.getUser()?.getIdToken(true)
            ?.addOnSuccessListener { tokenFb ->
                userViewModel.getUserByLavadaId(tokenFb.token.toString(), companionId)
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


    //это еще сделать нужно
    private fun updateUiCompanion(user: User) {
        with(viewBinding) {
            tvProfileName.text = user.name
            tvSubMessageName.text = String.format(
                resources.getString(R.string.location_and_dist),
                user.location,
                user.dist?.roundToInt().toString()
            )
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

//                    chatOpen?.let {
//                        it.messages.add(
//                            ChatMessage(
//                                message = textInputLayout.text.toString(),
//                                sender = selfUid,
//                                viewed = false,
//                                mediaUrl = "link1"
//                            )
//                        )
////                        messageChatViewModel.sendMessage(
////                            uidChat = chatId,
////                            chat = it
////                        )
//                    }
                    val mes = viewBinding.textInputLayout.text.toString()
                    firebaseEmailAuthService.getUser()?.getIdToken(true)
                        ?.addOnSuccessListener { tokenFb ->
                            messageChatViewModel.sendRemoteMessage(
                                tokenFb.token.toString(),
                                chatId,
                                mes
                            )

//                        }
                        }
                    textInputLayout.setText("")
                }
            }

            tbBackAction.setOnClickListener {
                parentFragmentManager.popBackStack()

            }

            btnInfo.setOnClickListener {

            }

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
            btnInfo.setOnClickListener {
                openCompanionInfo(companion)

            }
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

    private fun openCompanionInfo(user: User) {
        val fragment = CompanionInfoFragment.newInstance(user)
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val CHAT_ID: String = "chat_id"
        fun newInstance(chatId: String): FragmentOpenChat {
            val fragment = FragmentOpenChat()
            fragment.arguments = bundleOf(CHAT_ID to chatId)
            return fragment
        }
    }

}