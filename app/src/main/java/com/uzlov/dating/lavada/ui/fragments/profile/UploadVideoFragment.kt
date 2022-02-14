package com.uzlov.dating.lavada.ui.fragments.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.databinding.FragmentUploadVideoBinding
import com.uzlov.dating.lavada.storage.FirebaseStorageService
import com.uzlov.dating.lavada.storage.URIPathHelper
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import javax.inject.Inject

class UploadVideoFragment :
    BaseFragment<FragmentUploadVideoBinding>(FragmentUploadVideoBinding::inflate) {

    @Inject
    lateinit var firebaseStorageService: FirebaseStorageService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().appComponent.inject(this)
        viewBinding.btnAddVideo.setOnClickListener {
            if (checkPermission()) {
                openGalleryForVideo()

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            if (data?.data != null) {
                val uriPathHelper = URIPathHelper()
                val videoFullPath =
                    data.data?.let { context?.let { it1 -> uriPathHelper.getPath(it1, it) } }
                if (videoFullPath != null) {
                    firebaseStorageService.uploadVideo(videoFullPath.toString())
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity!!,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
            false
        } else true
    }

    private fun openGalleryForVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_CODE)
    }


    companion object {
        const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        const val REQUEST_CODE = 1

        fun newInstance() =
            UploadVideoFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}