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
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.Constants.Companion.ANOTHER
import com.uzlov.dating.lavada.app.Constants.Companion.MAN
import com.uzlov.dating.lavada.app.Constants.Companion.WOMAN
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentPersonalInfoBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.storage.IStorage
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject


class PersonalInfoFragment :
    BaseFragment<FragmentPersonalInfoBinding>(FragmentPersonalInfoBinding::inflate) {


    lateinit var list: List<Uri>
    private var userThis: User = User()
    private var urlImage: Bitmap? = null
    // var blackList: MutableList<String> = mutableListOf("WJfUC7k7EVZDtJdJYXDFn8DfAoD3", "67r4Wd1H9JblvgKhScAX3Y42aFX2", "NelQeaw9g4dBWT8oPuCiNrRdK0m2")

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var serverStorageService: IStorage

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
                    urlImage?.let { sendFileRequest(it) }
                    uri?.path?.let { loadImage(it, viewBinding.ivProfile) }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = factoryViewModel.create(UsersViewModel::class.java)
        checkPermission()
        firebaseEmailAuthService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
            model.authRemoteUser(hashMapOf("token" to tokenFb.token))
                .observe(viewLifecycleOwner) { tokenBack ->
                    model.getUser(tokenBack).observe(viewLifecycleOwner) { result ->
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
                                    2 -> radioGroup.check(R.id.rbAnother)
                                }
                                tvAgeValue.text = user.age?.toString()
                                slAge.value = user.age?.toFloat() ?: 18F
                            }
                        }
                    }
                }
        }

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

    private fun sendFileRequest(image: Bitmap) {

        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val body = MultipartBody.Part.createFormData(
            "user_photo",
            "user_photo",
            byteArray.toRequestBody("image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
        )
        firebaseEmailAuthService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
            model.authRemoteUser(hashMapOf("token" to tokenFb.token))
                .observe(viewLifecycleOwner) { tokenBack ->
                    model.updateRemoteData(tokenBack, body)
                }
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
                    R.id.rbAnother -> ANOTHER
                    else -> MAN
                }
                userThis.password = sex
            }

            btnCancel.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            btnSave.setOnClickListener {
                firebaseEmailAuthService.getUser()?.getIdToken(true)
                    ?.addOnSuccessListener { tokenFb ->
                        model.authRemoteUser(hashMapOf("token" to tokenFb.token))
                            .observe(viewLifecycleOwner) { tokenBack ->
                                val body = mutableMapOf<String, String>()
                                body["user_age"] = tvAgeValue.text.toString()
                                body["user_gender"] = userThis.password.toString()
                                body["user_nickname"] = tiEtName.text.toString()
                                /**описание не подгружается пока. Надо разбираться с бэкендером*/
                                body["user_description"] = tiEtAboutMyself.text.toString()
                                model.updateUser(tokenBack, body)
                                parentFragmentManager.popBackStack()
                            }
                    }
            }
        }
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
}