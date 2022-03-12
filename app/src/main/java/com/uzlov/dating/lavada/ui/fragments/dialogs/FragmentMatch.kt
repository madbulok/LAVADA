package com.uzlov.dating.lavada.ui.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.FragmentMatchBottomLayoutBinding
import com.uzlov.dating.lavada.domain.models.User


class FragmentMatch : BottomSheetDialogFragment() {
    private var viewBinding: FragmentMatchBottomLayoutBinding?= null
    private var user: User? = null

    companion object {
        const val USER_KEY = "user"
        fun newInstance(user: User): FragmentMatch {
            val fragment = FragmentMatch().apply {
                arguments = bundleOf(USER_KEY to user)
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        requireArguments().let {
            user = it.getParcelable(USER_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBinding = FragmentMatchBottomLayoutBinding.inflate(inflater, container, false)
        return viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding?.btnSendMessageTo?.setOnClickListener {

        }
        viewBinding?.ivClose?.setOnClickListener {
            dismiss()
        }
        viewBinding?.let {

            it.tvNameProfile.text = "${user?.name} ${user?.age}"
            it.tvLocationProfile.text = user?.location
            Glide.with(requireContext())
                .load(user?.url_avatar ?: "")
                .into(it.ivRandomProfile)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }
}