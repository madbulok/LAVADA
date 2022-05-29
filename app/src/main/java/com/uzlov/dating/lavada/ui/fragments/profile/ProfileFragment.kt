package com.uzlov.dating.lavada.ui.fragments.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gowtham.library.utils.LogMessage
import com.gowtham.library.utils.TrimType
import com.gowtham.library.utils.TrimVideo
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

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK &&
                result.data != null
            ) {
                val uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.data))
                path = uri.path
                (requireActivity() as HostActivity).showPreviewVideo(path ?: "", 1, user)
            } else {
                LogMessage.v("videoTrimResultLauncher data is null")
                Toast.makeText(context, "Что-то пошло не так, попробуйте еще", Toast.LENGTH_SHORT)
                    .show()
            }
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
        firebaseEmailAuthService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
            model.authRemoteUser(hashMapOf("token" to tokenFb.token))
                .observe(viewLifecycleOwner) { tokenBack ->
                    model.getUser(tokenBack).observe(viewLifecycleOwner) { result ->
                        user = result?.copy()!!
                        viewBinding.tvLocation.text = result.location
                        viewBinding.tvName.text = result.name + ", " + result.age
                        result.url_avatar?.let { it1 -> loadImage(it1, viewBinding.ivProfile) }
                    }
                    model.getRemoteBalance(tokenBack).observe(viewLifecycleOwner) { balance ->
                        viewBinding.btnCoins.text = balance.toString()
                    }
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
            clPremium.setOnClickListener {
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
                        TrimVideo.activity(list[0].toString())
                            .setHideSeekBar(true)
                            .setTrimType(TrimType.MIN_MAX_DURATION)
                            .setAccurateCut(true)
                            .setMinToMax(1, 5)
                            .start(this@ProfileFragment, startForResult)
                    }
                }
            }
        }

    fun openGalleryForVideo() {
        val intent = Intent().apply {
            type = "video/*"
            action = Intent.ACTION_PICK
        }
        startForResultOpenVideo.launch(intent)
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