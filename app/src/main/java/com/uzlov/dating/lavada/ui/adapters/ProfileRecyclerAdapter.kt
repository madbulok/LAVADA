package com.uzlov.dating.lavada.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.Player
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.TiktokTimelineItemRecyclerBinding
import com.uzlov.dating.lavada.domain.models.User
import java.util.*
import kotlin.math.roundToInt

class ProfileRecyclerAdapter(
    private var modelList: List<User>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    PlayerStateCallback {
    private var mItemClickListener: OnItemClickListener? = null
    private var actionListener: OnActionListener? = null

    interface OnActionListener {
        fun sendGift(user: User)
        fun sendHeart(user: User)
        fun sendMessage(user: User)
        fun complain(user: User)
    }

    fun updateList(modelList: List<User>) {
        this.modelList = modelList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int,
    ): VideoPlayerViewHolder {
        val binding: TiktokTimelineItemRecyclerBinding =
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context),
                R.layout.tiktok_timeline_item_recycler,
                viewGroup,
                false)
        return VideoPlayerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
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

    fun setOnActionClickListener(mActionClickListener: OnActionListener?) {
        actionListener = mActionClickListener
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        this.mItemClickListener = mItemClickListener
    }

    fun removeActionListener() {
        actionListener = null
    }

    fun removeOnItemSelectListener() {
        mItemClickListener = null
    }

    interface OnItemClickListener {
        fun onItemClick(
            position: Int,
            model: User?,
        )
    }

    inner class VideoPlayerViewHolder(private val binding: TiktokTimelineItemRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: User) {

            binding.root.setOnClickListener(object : DoubleClickListener() {
                override fun onDoubleClick(v: View) {
                    mItemClickListener?.onItemClick(
                        adapterPosition,
                        model
                    )
                }
            })
           Glide.with(binding.root)
                .load(model.url_avatar)
                .error(R.drawable.ic_default_user)
                .into(binding.ivRandomProfile)

            //было бы неплохо уточнить, какие именно цифры показываем
            binding.tvNameProfile. text = model.name + ", " + model.age
            binding.tvLocationProfile.text = "В " + model.dist?.roundToInt().toString() + " км от вас"
            binding.tvDescriptionProfile.text = model.about

            binding.ivHeartTo.setOnClickListener {
                actionListener?.sendHeart(model)
            }
            binding.ivGiftTo.setOnClickListener {
                actionListener?.sendGift(model)
            }
            binding.ivMessageTo.setOnClickListener {
                actionListener?.sendMessage(model)
            }
            binding.ivComplain.setOnClickListener {
                actionListener?.complain(model)
            }

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

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
//        removeOnItemSelectListener()
//        removeActionListener()
    }
}