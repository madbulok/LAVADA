package com.uzlov.dating.lavada.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uzlov.dating.lavada.databinding.FragmentChatsLayoutBinding
import com.uzlov.dating.lavada.databinding.ItemProfileStoriesLayoutBinding

class UsersChatsAdapter: RecyclerView.Adapter<UsersChatsAdapter.ChatViewHolder>() {

    private val stories = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding: FragmentChatsLayoutBinding =
            FragmentChatsLayoutBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.onBind(stories[position])
    }

    override fun getItemCount(): Int = stories.size

    inner class ChatViewHolder(private val binding: FragmentChatsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: String) {


        }
    }
}