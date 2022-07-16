package com.uzlov.dating.lavada.ui.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.FragmentBuyPremiumBinding

class FragmentBuyPremium (private var listener: OnSelectListener? = null) : BottomSheetDialogFragment() {
        private var viewBinding: FragmentBuyPremiumBinding?= null

        companion object {
            fun newInstance(): FragmentBuyPremium {
                return FragmentBuyPremium()
            }
        }

        interface OnSelectListener{
            fun oneMonth()
            fun sixMonth()
            fun oneYear()
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
            viewBinding = FragmentBuyPremiumBinding.inflate(inflater, container, false)
            return viewBinding!!.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            viewBinding?.ivClose?.setOnClickListener {
                dismiss()
            }
            viewBinding?.buyOneMonthPrice?.setOnClickListener {
                listener?.oneMonth()
                Toast.makeText(context, "Покупаем премиум на 1 месяц", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            viewBinding?.buySixMonthPrice?.setOnClickListener {
                listener?.sixMonth()
                Toast.makeText(context, "Покупаем премиум на 6 месяцев", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            viewBinding?.buyOneYearPrice?.setOnClickListener {
                listener?.oneYear()
                Toast.makeText(context, "Покупаем премиум на 1 год", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            viewBinding = null
            listener = null
        }
}