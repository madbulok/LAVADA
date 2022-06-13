package com.uzlov.dating.lavada.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.SkuDetails
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.app.Extensions
import com.uzlov.dating.lavada.databinding.CardBuyCoinBinding

class CoinsBoxAdapter(private var coinBoxClickListener: OnCoinBoxClickListener? = null) :
    RecyclerView.Adapter<CoinsBoxAdapter.CoinBoxViewHolder>() {

    private val gift = mutableListOf<SkuDetails>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinBoxViewHolder {
        val binding: CardBuyCoinBinding =
            CardBuyCoinBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CoinBoxViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinBoxViewHolder, position: Int) {
        holder.onBind(gift[position])
    }

    override fun getItemCount(): Int = gift.size

    fun setCoinBoxs(data: List<SkuDetails>) {
        gift.clear()
        gift.addAll(data)
        notifyDataSetChanged()
    }

    inner class CoinBoxViewHolder(private val binding: CardBuyCoinBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(box: SkuDetails) {
            with(binding) {
                coinsQuantity.text = box.title.split(" ").firstOrNull() ?: ""
                coinsNewPrice.text = box.price
                tvSaleCoins.text = Extensions.getSaleForPurchase(box.sku)

                Glide.with(itemView.context)
                    .load(Extensions.getPictureForPurchase(box.sku))
                    .into(ivCoinBox)
                itemView.setOnClickListener {
                    coinBoxClickListener?.onClick(box)
                }
            }
        }
    }

    interface OnCoinBoxClickListener {
        fun onClick(gift: SkuDetails)
    }
}
