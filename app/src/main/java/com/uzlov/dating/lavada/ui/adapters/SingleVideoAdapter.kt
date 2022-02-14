package com.uzlov.dating.lavada.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uzlov.dating.lavada.databinding.QuestionnaireItemViewBinding
import com.uzlov.dating.lavada.domain.models.User

class SingleVideoAdapter : RecyclerView.Adapter<SingleVideoAdapter.VideoViewHolder>() {

    private val allListedHistory = mutableListOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return VideoViewHolder(QuestionnaireItemViewBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.onBind(allListedHistory[position])
    }

    override fun getItemCount(): Int = allListedHistory.size

    fun setVideos(testData: List<User>) {
        allListedHistory.clear()
        allListedHistory.addAll(testData)
        notifyDataSetChanged()
    }

    fun nextProfile(profile: User) {
        allListedHistory.add(profile)
        notifyItemInsertToEnd()
    }

    inner class VideoViewHolder(val view: QuestionnaireItemViewBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun onBind(profile: User) {
            view.uidTest.text = profile.uid
        }
    }
}

fun <T : RecyclerView.ViewHolder> RecyclerView.Adapter<T>.notifyItemInsertToEnd() {
    notifyItemInserted(itemCount - 1)
}