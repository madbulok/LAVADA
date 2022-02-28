package com.uzlov.dating.lavada.ui.fragments.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.FragmentPreviewVideoBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.storage.FirebaseStorageService
import com.uzlov.dating.lavada.ui.activities.LoginActivity
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject

class PreviewVideoFragment : BaseFragment<FragmentPreviewVideoBinding>(FragmentPreviewVideoBinding::inflate) {

    @Inject
    lateinit var firebaseStorageService: FirebaseStorageService

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    @Inject
    lateinit var model: ViewModelFactory

    lateinit var userViewModel: UsersViewModel

    private var user: User = User()
    private var path: String = "" // local path

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        userViewModel = model.create(UsersViewModel::class.java)

        requireArguments().getParcelable<User>("user")?.let {
            user = it.copy()
        }
        requireArguments().getString("path")?.let {
            path = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding){
            btnBack.setOnClickListener {
                (requireActivity() as LoginActivity).rollbackFragment()
            }
            btnNext.setOnClickListener {
                val result = firebaseStorageService.uploadVideo(path)
                result.first.addOnSuccessListener {
                    user.url_video = result.second.toString()
                    userViewModel.addUser(user)
                    preferenceRepository.readUser()?.let {
                        preferenceRepository.updateUser(it.copy(isReady = true))
                    }
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            }
        }
    }

    companion object {
        fun newInstance(_path: String, user: User): PreviewVideoFragment {
            val args = bundleOf("user" to user, "path" to _path)
            val fragment = PreviewVideoFragment()
            fragment.arguments = args
            return fragment
        }
    }
}