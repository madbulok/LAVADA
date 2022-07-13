package com.uzlov.dating.lavada.ui.fragments

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import javax.inject.Inject


class ChatsFragment :
    BaseFragment<FragmentChatsLayoutBinding>(FragmentChatsLayoutBinding::inflate) {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var messageChatViewModel: MessageChatViewModel
    private lateinit var userViewModel: UsersViewModel

    @Inject
    lateinit var auth: FirebaseEmailAuthService

    private var companionId: String? = null
    private var chatId: String? = null
    private var companion: String? = null

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
                auth.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFB ->
                    chatId?.let { messageChatViewModel.getChatById(tokenFB.token.toString(), it) }
                }
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
        userViewModel = viewModelFactory.create(UsersViewModel::class.java)
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
        loadImage(resources.getDrawable(R.drawable.chats_clear), viewBinding.ivClearChat)

        // загружаем все чаты
        loadAllChats()
        messageChatViewModel.chatListData.observe(viewLifecycleOwner){ chatList ->
            renderUi(chatList)
            Log.e("ЧАТЫ:", chatList.toString())

        }
        messageChatViewModel.companionData.observe(viewLifecycleOwner){ comp ->
            companion = comp
        }

    }



    private fun loadAllChats() {
            auth.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
                messageChatViewModel.getRemoteChats(tokenFb.token.toString())

            }
    }

//   mapper [chat1(userTom, self), chat2(userArtem, self)]    --->    [ userTom(chat1), userArtem(chat2).....user_K(chat_N) ]
//   Map<CompanionUID, Chat>
    private fun mapResult(result: List<Chat>): Map<String, Chat> {
        return result.associateBy { chat -> chat.members.first { it != auth.getUserUid() } }
    }

    private fun renderUi(chats: List<MappedChat>) {
        if (!chats.isNullOrEmpty()) {
            viewBinding.chatRecyclerView.visibility = View.VISIBLE
            viewBinding.ivClearChat.visibility = View.GONE
            viewBinding.tvClearChat.visibility = View.GONE
            chatAdapter.setChats(chats)
        } else {
            viewBinding.ivClearChat.visibility = View.VISIBLE
            viewBinding.tvClearChat.visibility = View.VISIBLE
            viewBinding.chatRecyclerView.visibility = View.GONE
            Toast.makeText(requireContext(), "Чатов пока нет!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadImage(image: Drawable, container: ImageView) {
        view?.let {
            Glide
                .with(it.context)
                .load(image)
                .error(R.drawable.ic_default_user)
                .into(container)
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

            val swipeHelper = object : SwipeHelper(requireContext(), chatRecyclerView) {
                override fun instantiateUnderlayButton(
                    viewHolder: RecyclerView.ViewHolder?,
                    underlayButtons: MutableList<UnderlayButton>,
                ) {
                    underlayButtons.add(UnderlayButton(
                        "Block",
                        0,
                        Color.parseColor("#007AFF"),
                        object : UnderlayButtonClickListener {
                            override fun onClick(pos: Int) {
                                Log.e("TAG", "onClick: ")
                                auth.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
                                    val blockUser = chatAdapter.getList()[pos].companion.uid
                                 userViewModel.setBlackList(tokenFb.token.toString(), blockUser, "1")

                                }
                            }
                        }
                    ))
                    underlayButtons.add(UnderlayButton(
                        "Delete",
                        0,
                        Color.parseColor("#FF3B30"),
                        object : UnderlayButtonClickListener {
                            override fun onClick(pos: Int) {
                                Log.e("TAG", "onClick: ")
                                Toast.makeText(context, "Чат будет удален", Toast.LENGTH_SHORT).show()
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