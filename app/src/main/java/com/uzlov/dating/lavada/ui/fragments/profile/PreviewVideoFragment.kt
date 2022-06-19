package com.uzlov.dating.lavada.ui.fragments.profile

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.uzlov.dating.lavada.app.Constants.Companion.MAN
import com.uzlov.dating.lavada.app.Constants.Companion.WOMAN
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.FragmentPreviewVideoBinding
import com.uzlov.dating.lavada.domain.models.AuthorizedUser
import com.uzlov.dating.lavada.domain.models.MALE
import com.uzlov.dating.lavada.domain.models.RemoteUser
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.storage.IStorage
import com.uzlov.dating.lavada.ui.activities.HostActivity
import com.uzlov.dating.lavada.ui.activities.LoginActivity
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject


class PreviewVideoFragment :
    BaseFragment<FragmentPreviewVideoBinding>(FragmentPreviewVideoBinding::inflate) {

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var serverStorageService: IStorage

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    @Inject
    lateinit var model: ViewModelFactory

    private lateinit var userViewModel: UsersViewModel
    private var resp: RemoteUser? = null
    private var user: User = User()
    private var path: String = "" // local path
    private var request: Int = 0
    private val player by lazy {
        val renderer = DefaultRenderersFactory(requireContext())
        val taskSElector = DefaultTrackSelector(requireContext())
        val loadControl = DefaultLoadControl()
        ExoPlayer.Builder(requireContext(), renderer)
            .setTrackSelector(taskSElector)
            .setLoadControl(loadControl)
            .build()

    }

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
        requireArguments().getInt("request").let {
            request = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding) {

            tvNameProfile.text = user.name + ", " + user.age
            tvLocationProfile.text = user.location
            userViewModel.uploadedFileData.observe(viewLifecycleOwner) {
                resp = it
                endLoading()
                if (request == 1) {
                    (requireActivity() as HostActivity).rollbackFragment()
                } else {
                    (requireActivity() as LoginActivity).routeToMainScreen()
                }
                Toast.makeText(
                    requireContext(),
                    "Ваше видео успешно обновлено",
                    Toast.LENGTH_SHORT
                ).show()
            }
            btnBack.setOnClickListener {
                if (request == 1) {
                    (requireActivity() as HostActivity).rollbackFragmentWithFlag()
                } else {
                    (requireActivity() as LoginActivity).rollbackFragmentWithFlag()
                }
            }

            btnNext.setOnClickListener {
                if (request == 1) {
                    startLoading()
                    try {
                        createMultipartBodyPart(path)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        endWithErrorLoading()
                    }
                } else {
                    startLoading()
                    try {
                        user.ready = true
                        try {
                            val map = mutableMapOf<String, String>()
                            map["user_firstname"] = user.name.toString()
                            Log.e("POL", user.male.toString())
                            map["user_gender"] = when (user.male) {
                                MALE.MAN -> MAN
                                MALE.WOMAN -> WOMAN
                                else -> MAN
                            }
                            map["user_age"] = user.age.toString()
                            map["user_location_lat"] = user.lat.toString()
                            map["user_location_lng"] = user.lon.toString()
                            map["user_address"] = user.location.toString()
                            firebaseEmailAuthService.getUser()?.getIdToken(true)
                                ?.addOnSuccessListener { tokenFb ->
                                    userViewModel.updateUser(tokenFb.token.toString(), map)
                                }

                        } catch (e: Exception) {
                            e.printStackTrace()
                            endWithErrorLoading()
                        }
                        val localUser = preferenceRepository.readUser()
                        if (localUser != null) {
                            preferenceRepository.updateUser(
                                AuthorizedUser(
                                    localUser.uuid,
                                    localUser.datetime,
                                    localUser.name,
                                    true
                                )
                            )
                        } else {
                            val localUserNew = AuthorizedUser(
                                uuid = user.uid,
                                datetime = System.currentTimeMillis() / 1000,
                                name = user.name ?: "No name",
                                isReady = true
                            )
                            preferenceRepository.updateUser(localUserNew)
                        }
                        createMultipartBodyPart(path)

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        endWithErrorLoading()
                    }
                }
            }
            viewBinding.itemVideoExoplayer.player = player
            userViewModel.updatedUserData.observe(viewLifecycleOwner) {
                if (it.status != null) {
                    Log.d(TAG, "ok")
                } else {
                    Toast.makeText(context, userViewModel.status.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
            playVideo(path)
        }
    }

    private fun createMultipartBodyPart(path: String): RemoteUser? {
        val videoFile = File(path)
        val requestBody = videoFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("user_video", videoFile.name, requestBody)
        firebaseEmailAuthService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
            userViewModel.updateRemoteData(tokenFb.token.toString(), body)
        }
        return resp
    }

    private fun playVideo(path: String) {
        val videoFile = File(path)
        val pathVideo = Uri.fromFile(videoFile).toString()

        val uri = Uri.parse(pathVideo)


        player.playWhenReady = true
        val mediaItem =
            MediaItem.fromUri(uri)
        val mediaSource = ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(
                requireActivity()
            )
        ).createMediaSource(mediaItem)

        player.setMediaSource(mediaSource)
        player.prepare()
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.playWhenReady = true
    }

    private fun startLoading() {
        with(viewBinding) {
            btnNext.isEnabled = false
            progressUploading.visibility = View.VISIBLE
            btnBack.isEnabled = false
        }
    }

    private fun endLoading() {
        with(viewBinding) {
            btnNext.isEnabled = true
            progressUploading.visibility = View.INVISIBLE
            btnBack.isEnabled = true
        }
    }

    private fun endWithErrorLoading() {
        with(viewBinding) {
            btnNext.isEnabled = false
            progressUploading.visibility = View.INVISIBLE
            btnBack.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.stop()
        player.release()
    }

    companion object {
        fun newInstance(_path: String, user: User): PreviewVideoFragment {
            val args = bundleOf("user" to user, "path" to _path)
            val fragment = PreviewVideoFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(_path: String, request: Int, user: User): PreviewVideoFragment {
            val args = bundleOf("request" to request, "path" to _path, "user" to user)
            val fragment = PreviewVideoFragment()
            fragment.arguments = args
            return fragment
        }
    }
}

