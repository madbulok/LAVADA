package com.uzlov.dating.lavada.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.ItemBlackListLayoutBinding
import com.uzlov.dating.lavada.domain.models.User

class BlackListAdapter(private var chatClickListener: OnBlackListClickListener? = null) :
    RecyclerView.Adapter<BlackListAdapter.BlackListViewHolder>() {

    private val blackList = mutableListOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlackListViewHolder {
        val binding: ItemBlackListLayoutBinding =
            ItemBlackListLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return BlackListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlackListViewHolder, position: Int) {
        holder.onBind(blackList[position])
    }

    override fun getItemCount(): Int = blackList.size

    fun setBlackList(blocked: List<User>) {
        blackList.clear()
        blackList.addAll(blocked)
        notifyDataSetChanged()
    }

    inner class BlackListViewHolder(private val binding: ItemBlackListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: User) {
            with(binding) {
                tvProfileName.text = model.name + ", " + model.age
                tvSubMessageName.text = model.location
                ivUnlock.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) chatClickListener?.onClick(
                        blackList[adapterPosition]
                    )
                }
                model.url_avatar?.let { loadImage(root.context, it, imageProfile) }

            }
        }
    }

    private fun loadImage(context: Context, image: String, container: ImageView) {

        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 25f
        circularProgressDrawable.start()
        Glide
            .with(context)
            .load(image)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.ic_default_user)
            .into(container)

    }

    interface OnBlackListClickListener {
        fun onClick(blackList: User)
    }
}
