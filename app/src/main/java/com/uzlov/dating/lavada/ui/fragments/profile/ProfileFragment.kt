package com.uzlov.dating.lavada.ui.fragments.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentProfileBinding
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject

class ProfileFragment: BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var factoryViewModel: ViewModelFactory
    private lateinit var model: UsersViewModel

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
                viewBinding.tvAbout.text = "Ссылка на видео: " + user?.url_video
                viewBinding.tvAbout.text = "email: " + user?.email
                viewBinding.tvName.text = user?.name
            })
        }
        with(viewBinding){

            btnLogOut.setOnClickListener {
                firebaseEmailAuthService.logout()
                Toast.makeText(context, "Вы успешно вышли из аккаунта", Toast.LENGTH_SHORT).show()
            }
            btnDel.setOnClickListener {
                firebaseEmailAuthService.delUser()
                Toast.makeText(context, "Ваш аккаунт успешно удален!", Toast.LENGTH_SHORT).show()
            }
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