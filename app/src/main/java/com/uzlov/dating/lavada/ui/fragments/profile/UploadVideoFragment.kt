package com.uzlov.dating.lavada.ui.fragments.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.databinding.FragmentUploadVideoBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.storage.FirebaseStorageService
import com.uzlov.dating.lavada.storage.URIPathHelper
import com.uzlov.dating.lavada.ui.activities.LoginActivity
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import javax.inject.Inject

class UploadVideoFragment :
    BaseFragment<FragmentUploadVideoBinding>(FragmentUploadVideoBinding::inflate) {


    lateinit var list: List<Uri>
    private var path: String? = null
    private var user: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().getParcelable<User>("user")?.let {
            user = it.copy()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()
        viewBinding.btnSelectVideo.setOnClickListener {
            if (checkPermission()) {
                openGalleryForVideo()
            }
        }
        viewBinding.btnNext.setOnClickListener {
            path?.let {
                (requireActivity() as LoginActivity).showPreviewVideo(it, user)
            }
        }

        viewBinding.btnBack.setOnClickListener {
            (requireActivity() as LoginActivity).rollbackFragment()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            if (data?.data != null) {
                val uriPathHelper = URIPathHelper()
                val videoFullPath =
                    data.data?.let { context?.let { it1 -> uriPathHelper.getPath(it1, it) } }
                list = listOf(data.data) as List<Uri>
                if (videoFullPath != null) {
                    compressVideo()
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        val permission = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
            false
        } else true
    }

    private fun openGalleryForVideo() {
        val intent = Intent().apply {
            type = "video/*"
            action = Intent.ACTION_PICK
        }
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_CODE)
    }

    private fun compressVideo() {
        //нужно обновить UI (вопрос что там обновлять), работает на корутинах, не в основном потомке

        VideoCompressor.start(
            context = requireContext(), // => This is required
            uris = list, // => Source can be provided as content uris
            isStreamable = true,
            saveAt = Environment.DIRECTORY_MOVIES, // => the directory to save the compressed video(s)
            listener = object : CompressionListener {
                override fun onProgress(index: Int, percent: Float) {
                    // Update UI with progress value
                }

                override fun onStart(index: Int) {
                    // Compression start
                }

                override fun onSuccess(index: Int, size: Long, path: String?) {
                    this@UploadVideoFragment.path = path
                    viewBinding.btnNext.isEnabled = true
                    (requireActivity() as LoginActivity).showPreviewVideo(path ?: "", user)
                }

                override fun onFailure(index: Int, failureMessage: String) {
                    // On Failure
                    Log.d("Error", failureMessage)
                }

                override fun onCancelled(index: Int) {
                    // On Cancelled
                }

            },
            configureWith = Configuration(
                quality = VideoQuality.MEDIUM,
                frameRate = 24, /*Int, ignore, or null*/
                isMinBitrateCheckEnabled = true,
                videoBitrate = 3677198, /*Int, ignore, or null*/
                disableAudio = false, /*Boolean, or ignore*/
                keepOriginalResolution = true, /*Boolean, or ignore*/
            )
        )
    }

    companion object {
        const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        const val REQUEST_CODE = 1

        fun newInstance(user: User) =
            UploadVideoFragment().apply {
                arguments = bundleOf("user" to user)
            }
    }
}