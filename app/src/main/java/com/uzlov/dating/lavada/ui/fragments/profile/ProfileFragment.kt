package com.uzlov.dating.lavada.ui.fragments.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentProfileBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.domain.models.getNAmeLabel
import com.uzlov.dating.lavada.storage.URIPathHelper
import com.uzlov.dating.lavada.ui.activities.HostActivity
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.ui.fragments.ShopFragment
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentBuyPremium
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentSelectSourceVideo
import com.uzlov.dating.lavada.ui.fragments.settings.SettingsFragment
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import java.io.File
import javax.inject.Inject

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var factoryViewModel: ViewModelFactory
    private lateinit var model: UsersViewModel

    lateinit var list: List<Uri>
    private var path: String? = null
    private var user: User = User()
    private val settingsFragment by lazy {
        SettingsFragment()
    }

    private val personalInfo by lazy {
        PersonalInfoFragment()
    }
    private val shop by lazy {
        ShopFragment()
    }
    private val player by lazy {
        val renderer = DefaultRenderersFactory(requireContext())
        val taskSElector = DefaultTrackSelector(requireContext())
        val loadControl = DefaultLoadControl()
        ExoPlayer.Builder(requireContext(), renderer)
            .setTrackSelector(taskSElector)
            .setLoadControl(loadControl)
            .build()

    }

    private val selectVideoListener: FragmentSelectSourceVideo.OnSelectListener =
        object : FragmentSelectSourceVideo.OnSelectListener {
            override fun fromCamera() {
                (requireActivity() as HostActivity).startCaptureVideoFragment(1, user)
            }

            override fun fromDevice() {
                if (checkPermission()) {
                    openGalleryForVideo()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        model = factoryViewModel.create(UsersViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.flBackground)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                // handle onSlide
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    viewBinding.flCard.visibility = View.INVISIBLE
                    viewBinding.tvDesc.maxLines = 1
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    viewBinding.tvDesc.maxLines = Int.MAX_VALUE
                    viewBinding.flCard.visibility = View.VISIBLE

                }
            }
        })
        firebaseEmailAuthService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
            model.getUser(tokenFb.token.toString())
            model.selfUserData.observe(viewLifecycleOwner) { result ->
                user = result?.copy()!!
                viewBinding.tvLocation.text = result.location
                viewBinding.tvName.text = result.getNAmeLabel()
                viewBinding.tvDesc.text = result.about
                result.url_avatar?.let { it1 -> loadImage(it1, viewBinding.ivProfile) }
                viewBinding.itemVideoExoplayer.player = player
                result.url_video?.let { playVideo(it) }
                if (result.premium) {
                    viewBinding.clPremium.visibility = View.VISIBLE
                    viewBinding.clNotPremium.visibility = View.GONE
                } else {
                    viewBinding.clPremium.visibility = View.INVISIBLE
                    viewBinding.clNotPremium.visibility = View.VISIBLE
                }
            }
            viewBinding.tbBackAction.setOnClickListener {
                (requireActivity() as HostActivity).rollbackFragment()
            }
            model.getRemoteBalance(tokenFb.token.toString())
            model.selfBalanceData.observe(viewLifecycleOwner) { balance ->
                viewBinding.btnCoins.text = balance.toString()
            }
        }
        with(viewBinding) {
            ivEditProfile.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, personalInfo)
                    .addToBackStack(null)
                    .commit()
            }
            btnSettings.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, settingsFragment)
                    .addToBackStack(null)
                    .commit()
            }
            btnChangeVideo.setOnClickListener {
                FragmentSelectSourceVideo(selectVideoListener).show(
                    childFragmentManager,
                    FragmentSelectSourceVideo::class.java.simpleName
                )

            }
            btnTopUp.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, shop)
                    .addToBackStack(null)
                    .commit()
            }
            clNotPremium.setOnClickListener {
                val buyPremiumFragment = FragmentBuyPremium()
                buyPremiumFragment.show(
                    childFragmentManager,
                    buyPremiumFragment.javaClass.simpleName
                )
            }
            flCard.setOnClickListener {
                showCustomAlertToByPremium()
            }

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
        player.playWhenReady = true
    }

    private fun showCustomAlertToByPremium() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_layout, null)
        val customDialog = context?.let {
            MaterialAlertDialogBuilder(it, R.style.MaterialAlertDialog_rounded)
                .setView(dialogView)
                .show()
        }
        dialogView.findViewById<TextView>(R.id.description).text =
            getString(R.string.go_to_the_store_to_buy_premium)

        dialogView.findViewById<TextView>(R.id.header).text =
            getString(R.string.only_available_to_premium_accounts)


        val btnCancel = dialogView.findViewById<TextView>(R.id.btDismissCustomDialog)
        btnCancel.setOnClickListener {
            customDialog?.dismiss()
        }
        val btSendPass = dialogView.findViewById<Button>(R.id.btnSendPasswordCustomDialog)
        btSendPass.text = getString(R.string.go_to_shop)
        btSendPass.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, ShopFragment())
                .addToBackStack(null)
                .commit()
            customDialog?.dismiss()

        }
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

    private val startForResultOpenVideo =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data?.data != null) {
                    val uriPathHelper = URIPathHelper()
                    val videoFullPath =
                        result.data!!.data?.let { uriPathHelper.getPath(requireContext(), it) }
                    list = listOf(result.data!!.data) as List<Uri>
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
                Log.i("FFMPEG LOG : ", logMessage.text)
                startCompressing()
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun success() {
                stopCompressing()
                Log.e("OUTPUT", outputPath.toString())
                path = outputPath
                (requireActivity() as HostActivity).showPreviewVideo(path ?: "", 1, user)
            }

            override fun cancel() {
                stopCompressing()
            }

            override fun failed() {
                stopCompressing()
            }
        })
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
                    this@ProfileFragment.path = path
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
                                (requireActivity() as HostActivity).showPreviewVideo(
                                    path ?: "",
                                    1,
                                    user
                                )
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
                isMinBitrateCheckEnabled = true,
                videoBitrate = 3677198, /*Int, ignore, or null*/
                disableAudio = false, /*Boolean, or ignore*/
                keepOriginalResolution = true, /*Boolean, or ignore*/
                videoWidth = 1920.0, /*Double, ignore, or null*/
                videoHeight = 1080.0 /*Double, ignore, or null*/
            )
        )
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
                    startCompressing()
                }

                override fun onSuccess(index: Int, size: Long, path: String?) {
                    this@ProfileFragment.path = path
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
                                (requireActivity() as HostActivity).showPreviewVideo(
                                    path ?: "",
                                    1,
                                    user
                                )
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
                isMinBitrateCheckEnabled = true,
                videoBitrate = 3677198, /*Int, ignore, or null*/
                disableAudio = false, /*Boolean, or ignore*/
                keepOriginalResolution = true, /*Boolean, or ignore*/
            )
        )
    }

    fun startCompressing() {
        with(viewBinding) {
            btnChangeVideo.visibility = View.GONE
            progressCompressing.visibility = View.VISIBLE
        }
    }

    fun stopCompressing() {
        with(viewBinding) {
            btnChangeVideo.visibility = View.VISIBLE
            progressCompressing.visibility = View.GONE
        }
    }

    fun openGalleryForVideo() {
        val intent = Intent().apply {
            type = "video/*"
            action = Intent.ACTION_PICK
        }
        startForResultOpenVideo.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.stop()
    }

    companion object {
        const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}