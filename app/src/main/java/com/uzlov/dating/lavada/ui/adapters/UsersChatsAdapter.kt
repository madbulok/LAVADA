package com.uzlov.dating.lavada.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.ItemMessageLayoutBinding
import com.uzlov.dating.lavada.domain.models.Chat
import com.uzlov.dating.lavada.domain.models.MappedChat

class UsersChatsAdapter(private var chatClickListener: OnChatClickListener? = null) :
    RecyclerView.Adapter<UsersChatsAdapter.ChatViewHolder>() {

    private val stories = mutableListOf<MappedChat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding: ItemMessageLayoutBinding =
            ItemMessageLayoutBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        holder.onBind(stories[position])
    }

    override fun getItemCount(): Int = stories.size

    fun setChats(chats: List<MappedChat>) {
        stories.clear()
        stories.addAll(chats)
        notifyDataSetChanged()
    }

    fun getList(): List<MappedChat> {
        return stories
    }

    inner class ChatViewHolder(private val binding: ItemMessageLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(chat: MappedChat) {

            with(binding) {
                tvProfileName.text = "${chat.companion.name}, ${chat.companion.age}"
                tvSubMessageName.text = chat.chat.messages.lastOrNull()?.message ?: ""

                root.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        chatClickListener?.onClick(chat = chat.chat)
                    }
                    Glide.with(root.context)
                        .load(chat.companion.url_avatar)
                        .error(root.context.resources.getDrawable(R.drawable.test_avatar))
                        .into(imageProfile)
                }
            }
        }
    }

    interface OnChatClickListener {
        fun onClick(chat: Chat)
    }
}