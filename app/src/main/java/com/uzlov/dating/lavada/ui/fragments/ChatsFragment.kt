package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentChatsLayoutBinding
import com.uzlov.dating.lavada.domain.models.Chat
import com.uzlov.dating.lavada.ui.adapters.PlayerViewAdapter
import com.uzlov.dating.lavada.ui.adapters.UsersChatsAdapter
import com.uzlov.dating.lavada.ui.adapters.UsersProfileStoriesAdapter
import com.uzlov.dating.lavada.viemodels.MessageChatViewModel
import javax.inject.Inject

class ChatsFragment :
    BaseFragment<FragmentChatsLayoutBinding>(FragmentChatsLayoutBinding::inflate) {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var messageChatViewModel: MessageChatViewModel

    @Inject
    lateinit var auth: FirebaseEmailAuthService

    private var companionId: String? = null

    companion object {
        const val COMPANION_KEY = "companionId"
        fun newInstance(companionId: String): ChatsFragment {
            val fragment = ChatsFragment().apply {
                arguments = bundleOf(COMPANION_KEY to companionId)
            }
            return fragment
        }

        fun openChat(uidChat: String) {

        }
    }

    private val openChatCallback by lazy {
        object : UsersChatsAdapter.OnChatClickListener {
            override fun onClick(chat: Chat) {
                openChatFragment(chat)
            }
        }
    }

    val fragment = FragmentOpenChat()
    private fun openChatFragment(chat: Chat) {
        fragment.arguments = bundleOf(FragmentOpenChat.CHAT_ID to chat.uuid)
        parentFragmentManager.beginTransaction()
            .add(R.id.container, fragment)
            .hide(this)
            .show(fragment)
            .addToBackStack("open chat")
            .commit()
    }


    private val storiesAdapter by lazy {
        UsersProfileStoriesAdapter()
    }

    private val chatAdapter by lazy {
        UsersChatsAdapter(openChatCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        messageChatViewModel = viewModelFactory.create(MessageChatViewModel::class.java)
        requireArguments().let {
            companionId = it.getString(COMPANION_KEY, "") ?: ""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding) {
            historyRecyclerView.adapter = storiesAdapter
            chatRecyclerView.adapter = chatAdapter
        }
        initListeners()

        loadAllChats()
        if (!companionId.isNullOrEmpty()){
            loadChat(companionId!!)
        }
    }

    private fun loadChat(companionId: String) {
        auth.getUserUid()?.let {
            messageChatViewModel.createChat(selfId = it, companionId = companionId)

        }
    }

    private fun loadAllChats() {
        auth.getUserUid()?.let {
            messageChatViewModel.getChats(it)
                .observe(viewLifecycleOwner, { result ->
                    renderUi(result)
                })
        }
    }

    private fun renderUi(chats: List<Chat>?) {
        if (!chats.isNullOrEmpty()) {
            chatAdapter.setChats(chats)
        } else {
            Toast.makeText(requireContext(), "Чатов пока нет!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initListeners() {
        with(viewBinding) {
            tbBackAction.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            tbSearchAction.setOnClickListener {
                setSearchState(View.VISIBLE)
            }

            cancelSearch.setOnClickListener {
                setSearchState(View.GONE)
            }
        }
    }

    private fun setSearchState(visible: Int) {

        when (visible) {
            View.GONE -> {
                viewBinding.etSearchQuery.visibility = visible
                viewBinding.cancelSearch.visibility = visible
                viewBinding.chatLabel.visibility = View.VISIBLE
                viewBinding.tbSearchAction.visibility = View.VISIBLE
            }
            View.VISIBLE -> {
                viewBinding.etSearchQuery.visibility = visible
                viewBinding.cancelSearch.visibility = visible
                viewBinding.chatLabel.visibility = View.INVISIBLE
                viewBinding.tbSearchAction.visibility = View.INVISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PlayerViewAdapter.playCurrentPlayingVideo() // work but need testing!
    }
}