package com.uzlov.dating.lavada.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uzlov.dating.lavada.databinding.ItemProfileStoriesLayoutBinding

class UsersProfileStoriesAdapter(private var chatClickListener: OnStoriesClickListener? = null): RecyclerView.Adapter<UsersProfileStoriesAdapter.StoriesViewHolder>() {

    private val stories = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesViewHolder {
        val binding: ItemProfileStoriesLayoutBinding =
            ItemProfileStoriesLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoriesViewHolder, position: Int) {
        holder.onBind(stories[position])
    }

    override fun getItemCount(): Int = stories.size

    inner class StoriesViewHolder(private val binding: ItemProfileStoriesLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: String) {
            if (adapterPosition != RecyclerView.NO_POSITION) chatClickListener?.onClick(model)
        }
    }

    interface OnStoriesClickListener {
        fun onClick(story: String)
    }
}