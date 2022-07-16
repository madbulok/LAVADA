package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.databinding.FragmentCompanionInfoBinding
import com.uzlov.dating.lavada.domain.models.User
import kotlin.math.roundToInt

class CompanionInfoFragment: BaseFragment<FragmentCompanionInfoBinding>(FragmentCompanionInfoBinding::inflate) {

    private lateinit var user: User

    private val player by lazy {
        val renderer = DefaultRenderersFactory(requireContext())
        val taskSElector = DefaultTrackSelector(requireContext())
        val loadControl = DefaultLoadControl()
        ExoPlayer.Builder(requireContext(), renderer)
            .setTrackSelector(taskSElector)
            .setLoadControl(loadControl)
            .build()

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            it.getParcelable<User>(USER)?.let { _user ->
                user = _user.copy()
            }
        }
        with(viewBinding){
            tvNameProfile.text = user.name
            tvDescriptionProfile.text = user.about
            tvLocationProfile.text = "В " + user.dist?.roundToInt().toString() + " км от вас"
            itemVideoExoplayer.player = player


            viewForTap.setOnClickListener {
                if (tvDescriptionProfile.maxLines == 1){
                    tvDescriptionProfile.maxLines = Int.MAX_VALUE
                } else tvDescriptionProfile.maxLines = 1
            }
            tbBackAction.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
        user.url_avatar?.let { loadImage(it, viewBinding.ivRandomProfile) }
        user.url_video?.let { playVideo(it) }
    }

    private fun loadImage(image: String, container: ImageView) {
        val circularProgressDrawable = context?.let { CircularProgressDrawable(it) }
        circularProgressDrawable!!.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 25f
        circularProgressDrawable.start()

        view?.let {
            Glide
                .with(it.context)
                .load(image)
                .placeholder(circularProgressDrawable)
                .error(R.drawable.ic_default_user)
                .into(container)
        }
    }

    private fun playVideo(path: String) {
        val mediaItem =
            MediaItem.fromUri(path)
        val mediaSource = ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(
                requireActivity()
            )
        ).createMediaSource(mediaItem)

        player.setMediaSource(mediaSource)
        player.prepare()
        player.repeatMode = Player.REPEAT_MODE_ALL
        viewBinding.progressBar.visibility = View.GONE
        player.playWhenReady = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.stop()
    }
    companion object {
        private const val USER = "user"
        fun newInstance(user: User) =
            CompanionInfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(USER, user)
                }
            }
    }
}