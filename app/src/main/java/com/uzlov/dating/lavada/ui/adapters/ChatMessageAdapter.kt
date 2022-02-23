package com.uzlov.dating.lavada.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uzlov.dating.lavada.databinding.ItemMesageBinding
import com.uzlov.dating.lavada.domain.models.ChatMessage

class ChatMessageAdapter (private var chatClickListener: OnMessageClickListener? = null): RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder>() {

    private val stories = mutableListOf<ChatMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding: ItemMesageBinding =
            ItemMesageBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.onBind(stories[position])
    }

    override fun getItemCount(): Int = stories.size

    fun setMessages(chats: List<ChatMessage>) {
        stories.clear()
        stories.addAll(chats)
        notifyDataSetChanged()
    }

    inner class MessageViewHolder(private val binding: ItemMesageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: ChatMessage) {
            with(binding){
                messageText.text = model.message
                root.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) chatClickListener?.onClick(stories[adapterPosition])
                }
            }
        }
    }

    interface OnMessageClickListener {
        fun onClick(message: ChatMessage)
    }
}