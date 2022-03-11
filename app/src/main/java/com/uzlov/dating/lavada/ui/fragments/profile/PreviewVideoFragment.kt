package com.uzlov.dating.lavada.ui.fragments.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.FragmentPreviewVideoBinding
import com.uzlov.dating.lavada.domain.models.AuthorizedUser
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.storage.FirebaseStorageService
import com.uzlov.dating.lavada.ui.activities.LoginActivity
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import java.io.File
import javax.inject.Inject

class PreviewVideoFragment : BaseFragment<FragmentPreviewVideoBinding>(FragmentPreviewVideoBinding::inflate) {

    @Inject
    lateinit var firebaseStorageService: FirebaseStorageService

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    @Inject
    lateinit var model: ViewModelFactory

    lateinit var userViewModel: UsersViewModel

    private var user: User = User()
    private var path: String = "" // local path

    private val player by lazy {
        ExoPlayerFactory.newSimpleInstance(
            requireContext(),
            DefaultRenderersFactory(requireContext()),
            DefaultTrackSelector(), DefaultLoadControl())
    };

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        userViewModel = model.create(UsersViewModel::class.java)

        requireArguments().getParcelable<User>("user")?.let {
            user = it.copy()
        }
        requireArguments().getString("path")?.let {
            path = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding){

            tvLocationProfile.text = user.name

            btnBack.setOnClickListener {
                (requireActivity() as LoginActivity).rollbackFragment()
            }

            btnNext.setOnClickListener {

                val result = firebaseStorageService.uploadVideo(path)
                startLoading()
                result.first.addOnSuccessListener {
                    user.url_video = result.second.toString()
                    userViewModel.addUser(user)
                    val localUser = preferenceRepository.readUser()
                    if (localUser != null) {
                        preferenceRepository.updateUser(AuthorizedUser(localUser.uuid, localUser.datetime, localUser.name, true))
                    } else {
                        val localUserNew = AuthorizedUser(
                            uuid = user.uid,
                            datetime = System.currentTimeMillis() / 1000,
                            name = user.name ?: "No name",
                            isReady = true
                        )
                        preferenceRepository.updateUser(localUserNew)
                    }

                    endLoading()
                    (requireActivity() as LoginActivity).routeToMainScreen()
                }.addOnFailureListener {
                    it.printStackTrace()
                    endWithErrorLoading()
                }
            }
            viewBinding.itemVideoExoplayer.player = player

            playVideo(path)
        }
    }

    private fun playVideo(path: String) {
        val videoFile = File(path)
        val pathVideo = Uri.fromFile(videoFile).toString()

        val uri = Uri.parse(pathVideo)
        val mediaSource = ExtractorMediaSource.Factory(
            DefaultDataSourceFactory(
                requireActivity(), "exo-local"
            )
        ).createMediaSource(uri)

        player.prepare(mediaSource)
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.playWhenReady = true
    }

    private fun startLoading(){
        with(viewBinding){
            btnNext.isEnabled = false
            progressUploading.visibility = View.VISIBLE
            btnBack.isEnabled = false
        }
    }
    private fun endLoading(){
        with(viewBinding){
            btnNext.isEnabled = true
            progressUploading.visibility = View.INVISIBLE
            btnBack.isEnabled = true
        }
    }

    private fun endWithErrorLoading(){
        with(viewBinding){
            btnNext.isEnabled = false
            progressUploading.visibility = View.INVISIBLE
            btnBack.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.stop(false)
        player.release()
    }
    companion object {
        fun newInstance(_path: String, user: User): PreviewVideoFragment {
            val args = bundleOf("user" to user, "path" to _path)
            val fragment = PreviewVideoFragment()
            fragment.arguments = args
            return fragment
        }
    }
}