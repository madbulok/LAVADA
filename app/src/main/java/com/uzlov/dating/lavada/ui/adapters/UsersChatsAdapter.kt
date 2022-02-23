package com.uzlov.dating.lavada.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uzlov.dating.lavada.databinding.FragmentChatsLayoutBinding
import com.uzlov.dating.lavada.databinding.ItemMessageLayoutBinding
import com.uzlov.dating.lavada.domain.models.Chat

class UsersChatsAdapter(private var chatClickListener: OnChatClickListener? = null): RecyclerView.Adapter<UsersChatsAdapter.ChatViewHolder>() {

    private val stories = mutableListOf<Chat>()

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

    fun setChats(chats: List<Chat>) {
        stories.clear()
        stories.addAll(chats)
        notifyDataSetChanged()
    }

    inner class ChatViewHolder(private val binding: ItemMessageLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: Chat) {
            with(binding){
                tvProfileName.text = model.uuid
                root.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) chatClickListener?.onClick(stories[adapterPosition])
                }
            }
        }
    }

    interface OnChatClickListener {
        fun onClick(chat: Chat)
    }
}