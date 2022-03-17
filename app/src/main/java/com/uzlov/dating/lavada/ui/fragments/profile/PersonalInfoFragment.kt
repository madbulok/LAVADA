package com.uzlov.dating.lavada.ui.fragments.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentPersonalInfoBinding
import com.uzlov.dating.lavada.domain.models.MALE
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.storage.FirebaseStorageService
import com.uzlov.dating.lavada.storage.URIPathHelper
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject

class PersonalInfoFragment :
    BaseFragment<FragmentPersonalInfoBinding>(FragmentPersonalInfoBinding::inflate) {


    lateinit var list: List<Uri>
    private var userThis: User = User()
    private var urlImage: String = ""
    // var blackList: MutableList<String> = mutableListOf("WJfUC7k7EVZDtJdJYXDFn8DfAoD3", "67r4Wd1H9JblvgKhScAX3Y42aFX2", "NelQeaw9g4dBWT8oPuCiNrRdK0m2")

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var firebaseStorageService: FirebaseStorageService

    @Inject
    lateinit var factoryViewModel: ViewModelFactory
    private lateinit var model: UsersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UploadVideoFragment.REQUEST_CODE) {
            if (data?.data != null) {
                val uriPathHelper = URIPathHelper()
                val imageFullPath =
                    data.data?.let { context?.let { it1 -> uriPathHelper.getPath(it1, it) } }
                if (imageFullPath != null) {
                    val result = firebaseStorageService.uploadPhoto(imageFullPath)
                    result.first.addOnSuccessListener {
                        result.second.downloadUrl.addOnSuccessListener {
                            urlImage = it.toString()
                            loadImage(urlImage, viewBinding.ivProfile)
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = factoryViewModel.create(UsersViewModel::class.java)
        firebaseEmailAuthService.getUserUid()?.let {
            lifecycleScope.launchWhenResumed {
                model.getUserSuspend(it)?.let { result ->
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

    private fun initListeners() {
        with(viewBinding) {
            tbBackAction.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            btnAddPhoto.setOnClickListener {
                selectImageInAlbum()

            }
            ivEditPhoto.setOnClickListener {
                selectImageInAlbum()
            }
            slAge.addOnChangeListener { _, value, _ ->
                tvAgeValue.text = value.toInt().toString()
            }
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                val sex = when (checkedId) {
                    R.id.rbMan -> MALE.MAN
                    R.id.rvWoman -> MALE.WOMAN
                    R.id.rbAnother -> MALE.ANOTHER
                    else -> MALE.MAN
                }
                userThis.male = sex
            }

            btnCancel.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            btnSave.setOnClickListener {
                model.updateUser(userThis.uid, "age", tvAgeValue.text.toString().toInt())
                userThis.male?.let { it1 -> model.updateUser(userThis.uid, "male", it1) }
                model.updateUser(userThis.uid, "name", tiEtName.text.toString())
                model.updateUser(userThis.uid, "about", tiEtAboutMyself.text.toString())
                if (urlImage.isNotEmpty()) {
                    model.updateUser(userThis.uid, "url_avatar", urlImage)
                }
                parentFragmentManager.popBackStack()
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
        private const val REQUEST_TAKE_PHOTO = 0
        private const val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
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