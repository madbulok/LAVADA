package com.uzlov.dating.lavada.ui.swipes

interface ItemTouchHelperAdapter {
    fun onItemMove(from: Int, to: Int)
    fun onItemDismiss(position: Int)
}