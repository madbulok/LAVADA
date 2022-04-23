package com.uzlov.dating.lavada.ui.fragments.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentProfileBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.storage.URIPathHelper
import com.uzlov.dating.lavada.ui.activities.HostActivity
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.ui.fragments.ShopFragment
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentBuyPremium
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentSelectSourceVideo
import com.uzlov.dating.lavada.ui.fragments.settings.SettingsFragment
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
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
    private val selectVideoListener: FragmentSelectSourceVideo.OnSelectListener = object : FragmentSelectSourceVideo.OnSelectListener {
        override fun fromCamera() {
          //  (requireActivity() as LoginActivity).startCaptureVideoFragment(user)
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

        firebaseEmailAuthService.getUserUid()?.let { it ->
                model.getUser(it).observe(viewLifecycleOwner) { result ->
                    user = result?.copy()!!
                    viewBinding.tvLocation.text = result.location
                    viewBinding.tvName.text = result.name + ", " + result.age
                    viewBinding.btnCoins.text = result.balance.toString()
                    result.url_avatar?.let { it1 -> loadImage(it1, viewBinding.ivProfile) }
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
                FragmentSelectSourceVideo(selectVideoListener).show(childFragmentManager, FragmentSelectSourceVideo::class.java.simpleName)

            }
            btnTopUp.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, shop)
                    .addToBackStack(null)
                    .commit()
            }
            clPremium.setOnClickListener {
                val buyPremiumFragment = FragmentBuyPremium()
                buyPremiumFragment.show(childFragmentManager, buyPremiumFragment.javaClass.simpleName)
            }
            flCard.setOnClickListener {
                showCustomAlertToByPremium()
            }

        }
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
        view?.let {
            Glide
                .with(it.context)
                .load(image)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UploadVideoFragment.REQUEST_CODE) {
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

    private fun openGalleryForVideo() {
        val intent = Intent().apply {
            type = "video/*"
            action = Intent.ACTION_PICK
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select Video"),
            UploadVideoFragment.REQUEST_CODE
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
                    path?.let {
                        (requireActivity() as HostActivity).showPreviewVideo(path, 1)
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
                isMinBitrateCheckEnabled = false,
                videoBitrate = 3677198, /*Int, ignore, or null*/
                disableAudio = false, /*Boolean, or ignore*/
                keepOriginalResolution = true, /*Boolean, or ignore*/
            )
        )
    }

    fun startCompressing() {
        with(viewBinding){
            btnChangeVideo.visibility = View.GONE
            progressCompressing.visibility = View.VISIBLE
        }
    }

    fun stopCompressing() {
        with(viewBinding){
            btnChangeVideo.visibility = View.VISIBLE
            progressCompressing.visibility = View.GONE
        }
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