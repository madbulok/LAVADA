package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.GiftsBottomSheetDialogFragmentBinding
import com.uzlov.dating.lavada.ui.activities.HostActivity

class GiftsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _viewBinding: GiftsBottomSheetDialogFragmentBinding? = null
    private val viewBinding get() = _viewBinding!!

    companion object {

        const val TAG = "CustomBottomSheetDialogFragment"
        fun newInstance(): GiftsBottomSheetDialogFragment {
            val fragment = GiftsBottomSheetDialogFragment().apply {
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = GiftsBottomSheetDialogFragmentBinding.inflate(layoutInflater, container, false).also {
        _viewBinding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.let {
            val bottomSheet =
                it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.peekHeight = 370
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        with(viewBinding) {
            btnTopUp.setOnClickListener {
                (requireActivity() as HostActivity).startShopFragment()
            }
        }
    }
}