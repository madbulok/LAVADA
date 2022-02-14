package com.uzlov.dating.lavada.ui.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.Player
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.TiktokTimelineItemRecyclerBinding
import com.uzlov.dating.lavada.domain.models.User
import java.util.*

class ProfileRecyclerAdapter(
        private var modelList: List<User>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    PlayerStateCallback {
        private var mItemClickListener: OnItemClickListener? = null

        fun updateList(modelList: List<User>) {
            this.modelList = modelList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(
            viewGroup: ViewGroup,
            viewType: Int
        ): VideoPlayerViewHolder {
            val binding: TiktokTimelineItemRecyclerBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context)
                , R.layout.tiktok_timeline_item_recycler, viewGroup, false)
            return VideoPlayerViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int
        ) {

            //Here you can fill your row view
            if (holder is VideoPlayerViewHolder) {
                val model = getItem(position)
                val genericViewHolder = holder

                // send data to view holder
                genericViewHolder.onBind(model)
            }
        }

        override fun getItemCount(): Int {
            return modelList.size
        }

        private fun getItem(position: Int): User {
            return modelList[position]
        }

        override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
            val position = holder.adapterPosition
            PlayerViewAdapter.releaseRecycledPlayers(position)
            super.onViewRecycled(holder)
        }

        fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
            this.mItemClickListener = mItemClickListener
        }

        interface OnItemClickListener {
            fun onItemClick(
                position: Int,
                model: User?
            )
        }

        inner class VideoPlayerViewHolder(private val binding: TiktokTimelineItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
            fun onBind(model: User) {

                binding.root.setOnClickListener(object : DoubleClickListener() {
                    override fun onDoubleClick(v: View) {
                        mItemClickListener?.onItemClick(
                            adapterPosition,
                            model
                        )
                    }
                })

                binding.apply {
                    dataModel = model
                    callback = this@ProfileRecyclerAdapter
                    index = adapterPosition
                    executePendingBindings()
                }
            }
        }

        override fun onVideoDurationRetrieved(duration: Long, player: Player) {
        }

        override fun onVideoBuffering(player: Player) {
        }

        override fun onStartedPlaying(player: Player) {

        }

        override fun onFinishedPlaying(player: Player) {
        }
    }