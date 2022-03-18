package com.uzlov.dating.lavada.ui.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.FragmentBuyCoinsBinding

class FragmentBuyCoins (private var listener: OnSelectListener? = null) : BottomSheetDialogFragment() {
    private var viewBinding: FragmentBuyCoinsBinding?= null

    companion object {
        fun newInstance(): FragmentSelectSourceVideo {
            return FragmentSelectSourceVideo()
        }
    }

    interface OnSelectListener{
        fun coins300()
        fun coins500()
        fun coins1500()
        fun coins3500()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBinding = FragmentBuyCoinsBinding.inflate(inflater, container, false)
        return viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding?.ivClose?.setOnClickListener {
            dismiss()
        }
        viewBinding?.buy300CoinsPrice?.setOnClickListener {
            listener?.coins300()
            Toast.makeText(context, "Покупаем 300 монет", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        viewBinding?.buy500CoinsPrice?.setOnClickListener {
            listener?.coins500()
            Toast.makeText(context, "Покупаем 500 монет", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        viewBinding?.buy1500CoinsPrice?.setOnClickListener {
            listener?.coins1500()
            Toast.makeText(context, "Покупаем 1500 монет", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        viewBinding?.buy3500CoinsPrice?.setOnClickListener {
            listener?.coins3500()
            Toast.makeText(context, "Покупаем 3500 монет", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
        listener = null
    }
}