package com.uzlov.dating.lavada.ui.fragments.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.icu.util.Output
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.uzlov.dating.lavada.databinding.FragmentUploadVideoBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.storage.URIPathHelper
import com.uzlov.dating.lavada.ui.activities.HostActivity
import com.uzlov.dating.lavada.ui.activities.LoginActivity
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentSelectSourceVideo
import java.nio.file.Path


class UploadVideoFragment :
    BaseFragment<FragmentUploadVideoBinding>(FragmentUploadVideoBinding::inflate) {


    lateinit var list: List<Uri>
    private var path: String? = null
    private var user: User = User()

//    private val startForResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//            if (result.resultCode == Activity.RESULT_OK &&
//                result.data != null
//            ) {
//                val uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.data))
//                path = uri.path
//                /**
//                 * может тут на экран превью видео надо сразу?)
//                 * */
//                stopTrimming()
//
//            } else {
//                LogMessage.v("videoTrimResultLauncher data is null")
//                Toast.makeText(context, "Что-то пошло не так, попробуйте еще", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }

    private val selectVideoListener: FragmentSelectSourceVideo.OnSelectListener =
        object : FragmentSelectSourceVideo.OnSelectListener {
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
            FragmentSelectSourceVideo(selectVideoListener).show(
                childFragmentManager,
                FragmentSelectSourceVideo::class.java.simpleName
            )
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

    private val startForResultOpenVideo =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data?.data != null) {
                    val uriPathHelper = URIPathHelper()
                    val videoFullPath =
                        result.data!!.data?.let { uriPathHelper.getPath(requireContext(), it) }
                    list = listOf(result.data?.data) as List<Uri>
                    if (videoFullPath != null) {
                        val uri = Uri.parse(videoFullPath)
                        var videoWidth: Int
                        MediaPlayer.create(context, uri).also {
                            videoWidth = it.videoWidth
                            it.reset()
                            it.release()
                            if (videoWidth > 1920) {
                                compressVideoWithTrim()
                            } else {
                                compressVideo()
                            }
                        }

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
        startForResultOpenVideo.launch(intent)
    }


    private fun testTrim(inputPath: String) {
        val outputPath = context?.let { Common.getFilePath(it, Common.VIDEO) }
        val startTimeString = "00:00:00"
        val endTimeString = "00:00:05"
        val query: Array<String> =
            FFmpegQueryExtension().cutVideo(inputPath, startTimeString, endTimeString, outputPath!!)
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun statisticsProcess(statistics: com.simform.videooperations.Statistics) {
                Log.i("FFMPEG LOG : ", statistics.videoFrameNumber.toString())
            }

            override fun process(logMessage: com.simform.videooperations.LogMessage) {
                startCompressing()
                Log.i("FFMPEG LOG : ", logMessage.text)
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun success() {
                Log.e("OUTPUT", outputPath.toString())
                path = outputPath
                viewBinding.progressRegistration.setProgressCompat(100, true)
                viewBinding.btnSelectVideo.text = "Видео выбрано. Нажмите чтоб выбрать другое"
                stopCompressing()

            }

            override fun cancel() {
            }

            override fun failed() {
            }
        })
    }


    private fun compressVideo() {
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
                    if (path != null) {
                        val uri = Uri.parse(path)
                        var durationTime: Long
                        MediaPlayer.create(context, uri).also {
                            durationTime = (it.duration / 1000).toLong()
                            it.reset()
                            it.release()
                            if (durationTime > 5 && durationTime != 0L) {
                                testTrim(path)
                            } else {
                                viewBinding.progressRegistration.setProgressCompat(100, true)
                                viewBinding.btnSelectVideo.text = "Видео выбрано. Нажмите чтоб выбрать другое"
                                Log.e("РАЗМЕР ВИДЕО СТАЛ:", size.toString())
                            }
                        }
                    }

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

    private fun compressVideoWithTrim() {

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
                    startCompressing()
                }

                override fun onSuccess(index: Int, size: Long, path: String?) {
                    this@UploadVideoFragment.path = path
                    stopCompressing()
                    if (path != null) {
                        val uri = Uri.parse(path)
                        var durationTime: Long
                        MediaPlayer.create(context, uri).also {
                            durationTime = (it.duration / 1000).toLong()
                            it.reset()
                            it.release()
                            if (durationTime > 5 && durationTime != 0L) {
                                testTrim(path)
                            } else {
                                viewBinding.progressRegistration.setProgressCompat(100, true)
                                viewBinding.btnSelectVideo.text = "Видео выбрано. Нажмите чтоб выбрать другое"
                                Log.e("РАЗМЕР ВИДЕО СТАЛ:", size.toString())
                            }
                        }
                    }
                }

                override fun onFailure(index: Int, failureMessage: String) {
                    // On Failure
                    Toast.makeText(requireContext(), failureMessage, Toast.LENGTH_SHORT).show()
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
                videoWidth = 1920.0, /*Double, ignore, or null*/
                videoHeight = 1080.0 /*Double, ignore, or null*/
            )
        )
    }

    fun startCompressing() {
        with(viewBinding) {
            btnBack.isEnabled = false
            btnNext.isEnabled = false
            btnSelectVideo.visibility = View.GONE
            progressCompressing.visibility = View.VISIBLE
        }
    }

    fun stopCompressing() {
        with(viewBinding) {
            btnBack.isEnabled = true
            btnNext.isEnabled = true
            btnSelectVideo.visibility = View.VISIBLE
            progressCompressing.visibility = View.GONE
        }
    }

//    private fun stopTrimming() {
//        with(viewBinding) {
//            btnBack.isEnabled = true
//            btnNext.isEnabled = true
//            btnSelectVideo.visibility = View.VISIBLE
//            progressCompressing.visibility = View.GONE
//        }
//    }

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