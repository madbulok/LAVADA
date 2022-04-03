package com.uzlov.dating.lavada.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.ItemGiftLayoutBinding
import com.uzlov.dating.lavada.domain.models.Gift

class GiftsAdapter(private var chatClickListener: OnGiftsClickListener? = null) :
    RecyclerView.Adapter<GiftsAdapter.GiftsViewHolder>() {

    private val gift = mutableListOf<Gift>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftsViewHolder {
        val binding: ItemGiftLayoutBinding =
            ItemGiftLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return GiftsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GiftsViewHolder, position: Int) {
        holder.onBind(gift[position])
    }

    override fun getItemCount(): Int = gift.size

    fun setGifts(data: List<Gift>) {
        gift.clear()
        gift.addAll(data)
        notifyDataSetChanged()
    }

    inner class GiftsViewHolder(private val binding: ItemGiftLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: Gift) {
            with(binding) {
                btnCost.text = model.cost.toString()
                loadImage(root.context, model.descriptions, ivGift)
                btnCost.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) chatClickListener?.onClick(
                        gift[adapterPosition]
                    )
                }
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
            .error(R.drawable.ic_star_1)
            .into(container)

    }

    interface OnGiftsClickListener {
        fun onClick(gift: Gift)
    }
}
