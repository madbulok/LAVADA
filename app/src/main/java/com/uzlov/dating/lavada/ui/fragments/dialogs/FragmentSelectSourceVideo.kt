package com.uzlov.dating.lavada.ui.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.FragmentSourceVideoLayoutBinding


class FragmentSelectSourceVideo(private var listener: OnSelectListener? = null) : BottomSheetDialogFragment() {
    private var viewBinding: FragmentSourceVideoLayoutBinding?= null

    companion object {
        fun newInstance(): FragmentSelectSourceVideo {
            return FragmentSelectSourceVideo()
        }
    }

    interface OnSelectListener{
        fun fromCamera()
        fun fromDevice()
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
        viewBinding = FragmentSourceVideoLayoutBinding.inflate(inflater, container, false)
        return viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding?.ivClose?.setOnClickListener {
            dismiss()
        }
        viewBinding?.addVideoFromGallery?.setOnClickListener {
            listener?.fromDevice()
            dismiss()
        }
        viewBinding?.addVideoFromRecord?.setOnClickListener {
            listener?.fromCamera()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
        listener = null
    }
}