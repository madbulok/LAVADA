package com.uzlov.dating.lavada.ui.fragments.dialogs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.FragmentMatchBottomLayoutBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.ui.activities.SingleChatActivity
import kotlin.math.roundToInt


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

                    openChatActivity(user?.userId!!, user?.uid!!)

        }
        viewBinding?.ivClose?.setOnClickListener {
            dismiss()
        }
        viewBinding?.let {
            it.tvNameProfile.text = String.format(resources.getString(R.string.name_and_age), user?.name, user?.age.toString())
            it.tvLocationProfile.text =
                String.format(resources.getString(R.string.location_and_dist), user?.location, user?.dist?.roundToInt().toString())
            Glide.with(requireContext())
                .load(user?.url_avatar ?: "")
                .into(it.ivRandomProfile)
        }

    }

    private fun openChatActivity(companionId: String, companionUid: String) {
        val intent = Intent(requireContext(), SingleChatActivity::class.java).apply {
            putExtra(SingleChatActivity.COMPANION_ID, companionId)
            putExtra(SingleChatActivity.COMPANION_UID, companionUid)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }
}