package com.uzlov.dating.lavada.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentChatsLayoutBinding
import com.uzlov.dating.lavada.domain.models.Chat
import com.uzlov.dating.lavada.domain.models.MappedChat
import com.uzlov.dating.lavada.ui.adapters.PlayerViewAdapter
import com.uzlov.dating.lavada.ui.adapters.UsersChatsAdapter
import com.uzlov.dating.lavada.ui.adapters.UsersProfileStoriesAdapter
import com.uzlov.dating.lavada.ui.swipes.SwipeHelper
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
    private var chatId: String? = null

    companion object {
        const val COMPANION_KEY = "companionId"
        const val CHAT_ID: String = "chat_id"
        fun newInstance(companionId: String?, chatId: String?): ChatsFragment {
            val fragment = ChatsFragment().apply {
                arguments = bundleOf(
                    COMPANION_KEY to companionId,
                    CHAT_ID to chatId
                )
            }
            return fragment
        }
    }

    private val openChatCallback by lazy {
        object : UsersChatsAdapter.OnChatClickListener {
            override fun onClick(chat: Chat) {
                openChatFragment(chat.uuid)
            }
        }
    }

    val fragment = FragmentOpenChat()
    private fun openChatFragment(uuid: String) {
        fragment.arguments = bundleOf(FragmentOpenChat.CHAT_ID to uuid)
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
            chatId = it.getString(CHAT_ID, "") ?: ""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding) {
            historyRecyclerView.adapter = storiesAdapter
            chatRecyclerView.adapter = chatAdapter
        }
        initListeners()

        // загружаем все чаты
        loadAllChats()

        // если передали ID собеседника то загружаем с ним чат
        if (!companionId.isNullOrEmpty()){
            loadChat(companionId!!)
        }

        // если передали ID чата то загружаем этот чат
        if (!chatId.isNullOrEmpty()){
            openChatFragment(chatId!!)
        }
    }

    private fun loadChat(companionId: String) {
        auth.getUserUid()?.let {
            messageChatViewModel.createChat(selfId = it, companionId = companionId)

        }
    }

    private fun loadAllChats() {
        auth.getUserUid()?.let {
            messageChatViewModel.getChats(it, auth.getUserUid() ?: "")
                .observe(viewLifecycleOwner, { result ->
                    renderUi(result)
                })
        }
    }

//   mapper [chat1(userTom, self), chat2(userArtem, self)]    --->    [ userTom(chat1), userArtem(chat2).....user_K(chat_N) ]
//   Map<CompanionUID, Chat>
    private fun mapResult(result: List<Chat>): Map<String, Chat> {
        return result.associateBy { chat -> chat.members.first { it != auth.getUserUid() } }
    }

    private fun renderUi(chats: List<MappedChat>) {
        chatAdapter.setChats(chats)
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

            val swipeHelper = object : SwipeHelper(requireContext(), chatRecyclerView) {
                override fun instantiateUnderlayButton(
                    viewHolder: RecyclerView.ViewHolder?,
                    underlayButtons: MutableList<UnderlayButton>,
                ) {
                    underlayButtons.add(UnderlayButton(
                        getString(R.string.block),
                        0,
                        Color.parseColor("#007AFF"),
                        object : UnderlayButtonClickListener {
                            override fun onClick(pos: Int) {
                                Log.e("TAG", "onClick: ")
                            }
                        }
                    ))
                    underlayButtons.add(UnderlayButton(
                        getString(R.string.delete),
                        0,
                        Color.parseColor("#FF3B30"),
                        object : UnderlayButtonClickListener {
                            override fun onClick(pos: Int) {

                            }
                        }
                    ))
                }
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