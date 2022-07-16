package com.uzlov.dating.lavada.ui.fragments.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.Constants.Companion.MAN
import com.uzlov.dating.lavada.app.Constants.Companion.WOMAN
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentPersonalInfoBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import javax.inject.Inject


class PersonalInfoFragment :
    BaseFragment<FragmentPersonalInfoBinding>(FragmentPersonalInfoBinding::inflate) {


    lateinit var list: List<Uri>
    private var userThis: User = User()
    private var urlImage: Bitmap? = null
    private var fileSended: Boolean? = null

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var factoryViewModel: ViewModelFactory
    private lateinit var model: UsersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UploadVideoFragment.REQUEST_CODE) {

            data?.data.let { uri ->

                val imageUri: Uri? = uri
                val source =
                    context?.let { ImageDecoder.createSource(it.contentResolver, imageUri!!) }
                val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                if (bitmap != null) {
                    urlImage = bitmap
                    uri?.let { loadImageFromUri(it, viewBinding.ivProfile) }


                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = factoryViewModel.create(UsersViewModel::class.java)
        checkPermission()
        firebaseEmailAuthService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
            model.getUser(tokenFb.token.toString())
            model.selfUserData.observe(viewLifecycleOwner) { result ->
                result?.let { user ->
                    userThis = user
                    with(viewBinding) {
                        if (user.url_avatar.isNullOrEmpty()) {
                            btnAddPhoto.visibility = View.VISIBLE
                            ivEditPhoto.visibility = View.GONE
                        } else {
                            btnAddPhoto.visibility = View.GONE
                            ivEditPhoto.visibility = View.VISIBLE
                            loadImage(user.url_avatar.toString(), viewBinding.ivProfile)
                        }
                        tiEtName.setText(user.name)
                        tiEtAboutMyself.setText(user.about)
                        tiEtLocation.setText(user.location)
                        when (user.male?.ordinal) {
                            0 -> radioGroup.check(R.id.rbMan)
                            1 -> radioGroup.check(R.id.rvWoman)
                            else -> radioGroup.check(R.id.rbMan)
                        }
                        tvAgeValue.text = user.age?.toString()
                        slAge.value = user.age?.toFloat() ?: 18F
                    }
                }
            }
        }
        model.updatedUserData.observe(viewLifecycleOwner) { data ->
            if (data.status != null && fileSended != null) {
                model.uploadedFileData.observe(viewLifecycleOwner) { image ->
                    if (image.status != null && fileSended == true) {
                        endLoading()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(context, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (data.status != null && fileSended == null){
                endLoading()
                parentFragmentManager.popBackStack()
            } else Toast.makeText(context, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
        }
        addTextChangedListener()
        initListeners()
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

    private fun sendFileRequest(image: Bitmap){

        fileSended = false
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val bodyToo = MultipartBody.Part.createFormData(
            "user_photo",
            "user_photo",
            byteArray.toRequestBody("image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
        )
        firebaseEmailAuthService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
            model.updateRemoteData(tokenFb.token.toString(), bodyToo)
            fileSended = true
        }
    }

    private fun initListeners() {
        with(viewBinding) {
            tbBackAction.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            btnAddPhoto.setOnClickListener {
                if (checkPermission()) {
                    selectImageInAlbum()
                }
            }
            ivEditPhoto.setOnClickListener {
                if (checkPermission()) {
                    selectImageInAlbum()
                }
            }
            slAge.addOnChangeListener { _, value, _ ->
                tvAgeValue.text = value.toInt().toString()
            }
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                val sex = when (checkedId) {
                    R.id.rbMan -> MAN
                    R.id.rvWoman -> WOMAN
                    else -> MAN
                }
                userThis.password = sex
            }

            btnCancel.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            btnSave.setOnClickListener {
                startLoading()
                firebaseEmailAuthService.getUser()?.getIdToken(true)
                    ?.addOnSuccessListener { tokenFb ->
                        val body = mutableMapOf<String, String>()
                        body["user_age"] = tvAgeValue.text.toString()
                        body["user_gender"] = userThis.password.toString()
                        body["user_firstname"] = tiEtName.text.toString()
                        body["user_description"] = tiEtAboutMyself.text.toString()
                        model.updateUser(tokenFb.token.toString(), body)
                        if (urlImage != null){
                            sendFileRequest(urlImage!!)
                        }
                    }
            }
        }
    }

    private fun addTextChangedListener() {
        viewBinding.tiEtAboutMyself.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                verifyText()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun verifyText() {
        viewBinding.btnSave.isEnabled = viewBinding.tiEtAboutMyself.length() <= 200
    }

    private fun selectImageInAlbum() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_PICK
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select Photo"),
            REQUEST_SELECT_IMAGE_IN_ALBUM
        )
    }

    companion object {
        private const val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
        const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
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

    private fun loadImageFromUri(image: Uri, container: ImageView) {
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

    private fun startLoading() {
        with(viewBinding) {
            btnSave.isEnabled = false
            progressUploading.visibility = View.VISIBLE
        }
    }

    private fun endLoading() {
        with(viewBinding) {
            btnSave.isEnabled = true
            progressUploading.visibility = View.INVISIBLE
        }
    }
}