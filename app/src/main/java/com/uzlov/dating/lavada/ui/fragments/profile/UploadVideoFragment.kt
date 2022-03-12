package com.uzlov.dating.lavada.ui.fragments.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.uzlov.dating.lavada.databinding.FragmentUploadVideoBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.storage.URIPathHelper
import com.uzlov.dating.lavada.ui.activities.LoginActivity
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentSelectSourceVideo

class UploadVideoFragment :
    BaseFragment<FragmentUploadVideoBinding>(FragmentUploadVideoBinding::inflate) {


    lateinit var list: List<Uri>
    private var path: String? = null
    private var user: User = User()

    private val selectVideoListener: FragmentSelectSourceVideo.OnSelectListener = object : FragmentSelectSourceVideo.OnSelectListener {
        override fun fromCamera() {
            (requireActivity() as LoginActivity).startCaptureVideoFragment(user)
        }

        override fun fromDevice() {
            if (checkPermission()) {
                openGalleryForVideo()
            }
        }
    }

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
            FragmentSelectSourceVideo(selectVideoListener).show(childFragmentManager, FragmentSelectSourceVideo::class.java.simpleName)
        }

        viewBinding.btnNext.setOnClickListener {
            path?.let {
                (requireActivity() as LoginActivity).showPreviewVideo(path ?: "", user)
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
                    data.data?.let { uriPathHelper.getPath(requireContext(), it) }
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
                    viewBinding.progressRegistration.setProgressCompat(percent.toInt(), true)
                }

                override fun onStart(index: Int) {
                    // Compression start
                    viewBinding.progressRegistration.setProgressCompat(0, true)
                    startCompressing()
                }

                override fun onSuccess(index: Int, size: Long, path: String?) {
                    this@UploadVideoFragment.path = path
                    stopCompressing()
                    viewBinding.progressRegistration.setProgressCompat(100, true)
                    viewBinding.btnSelectVideo.text = "Видео выбрано. Нажмите чтоб выбрать другое"
                }

                override fun onFailure(index: Int, failureMessage: String) {
                    // On Failure
                    Toast.makeText(requireContext(), failureMessage, Toast.LENGTH_SHORT).show()
                    stopCompressing()
                }

                override fun onCancelled(index: Int) {
                    // On Cancelled
                    stopCompressing()
                }

            },
            configureWith = Configuration(
                quality = VideoQuality.MEDIUM,
                frameRate = 24, /*Int, ignore, or null*/
                isMinBitrateCheckEnabled = false,
                videoBitrate = 3677198, /*Int, ignore, or null*/
                disableAudio = false, /*Boolean, or ignore*/
                keepOriginalResolution = true, /*Boolean, or ignore*/
            )
        )
    }

    fun startCompressing() {
        with(viewBinding){
            btnBack.isEnabled = false
            btnNext.isEnabled = false
            btnSelectVideo.visibility = View.GONE
            progressCompressing.visibility = View.VISIBLE
        }
    }

    fun stopCompressing() {
        with(viewBinding){
            btnBack.isEnabled = true
            btnNext.isEnabled = true
            btnSelectVideo.visibility = View.VISIBLE
            progressCompressing.visibility = View.GONE
        }
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