package com.uzlov.dating.lavada.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uzlov.dating.lavada.databinding.ItemMesageRecievedBinding
import com.uzlov.dating.lavada.databinding.ItemMesageSendBinding
import com.uzlov.dating.lavada.domain.models.ChatMessage

class ChatMessageAdapter (private var chatClickListener: OnMessageClickListener? = null, private val self: String = ""): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SEND_TYPE = 1
    private val RECEIVED_TYPE = 2

    private val stories = mutableListOf<ChatMessage>()

    override fun getItemViewType(position: Int): Int {
        return if (stories[position].sender == self){
            SEND_TYPE
        } else {
            RECEIVED_TYPE
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SEND_TYPE){
            val binding: ItemMesageSendBinding =
                ItemMesageSendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MessageSendViewHolder(binding)
        } else {
            val binding: ItemMesageRecievedBinding =
                ItemMesageRecievedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MessageReceivingViewHolder(binding)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MessageReceivingViewHolder->{
                holder.onBind(stories[position])
            }
            is MessageSendViewHolder->{
                holder.onBind(stories[position])
            }
        }
    }

    override fun getItemCount(): Int = stories.size

    fun setMessages(chats: List<ChatMessage>) {
        stories.clear()
        stories.addAll(chats)
        notifyDataSetChanged()
    }

    inner class MessageSendViewHolder(private val binding: ItemMesageSendBinding) :
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

    inner class MessageReceivingViewHolder(private val binding: ItemMesageRecievedBinding) :
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