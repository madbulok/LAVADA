package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.databinding.FragmentViewerImageLayoutBinding

class ImageViewerFragment : BaseFragment<FragmentViewerImageLayoutBinding>(FragmentViewerImageLayoutBinding::inflate) {

    companion object {
        const val MEDIA_LINK: String = "mediaUrl"
        fun newInstance(mediaUrl: String): ImageViewerFragment {
            val args = bundleOf(mediaUrl to MEDIA_LINK)
            val fragment = ImageViewerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val url = it.getString(MEDIA_LINK)

            Glide.with(requireContext())
                .load(url)
                .into(viewBinding.imageView as ImageView)
        }

    }

}