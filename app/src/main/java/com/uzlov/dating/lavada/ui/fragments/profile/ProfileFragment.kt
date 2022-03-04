package com.uzlov.dating.lavada.ui.fragments.profile

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentProfileBinding
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.ui.fragments.settings.SettingsFragment
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject

class ProfileFragment: BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var factoryViewModel: ViewModelFactory
    private lateinit var model: UsersViewModel

    private val settingsFragment by lazy {
        SettingsFragment()
    }

    private val uploadVideoFragment by lazy {
        UploadVideoFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = factoryViewModel.create(UsersViewModel::class.java)

        //прочитать данные model.getUser(uid), uid - firebaseEmailAuthService.getUserUid()
        firebaseEmailAuthService.getUserUid()?.let {
            model.getUser(it)?.observe(this, { user ->
                viewBinding.tvLocation.text = "location " + user?.lat + ", " + user?.lon
                viewBinding.tvName.text = user?.name + ", " + user?.age
                user?.url_avatar?.let { it1 -> loadImage(it1, viewBinding.ivProfile) }

            })
        }
        with(viewBinding){


            btnSettings.setOnClickListener {
                parentFragmentManager.beginTransaction()
//                    .add(R.id.container, settingsFragment)
//                    .hide(this@com.uzlov.dating.lavada.ui.fragments.profile.ProfileFragment)
//                    .show(settingsFragment)
                    .replace(R.id.container, settingsFragment)
                    .addToBackStack(null)
                    .commit()
            }
            btnChangeVideo.setOnClickListener {
                Toast.makeText(context, "Переходим на фрагмент обновления видео", Toast.LENGTH_SHORT).show()
            }
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


    companion object {
        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}