package com.uzlov.dating.lavada.ui.fragments.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.data_sources.IUsersRepository
import com.uzlov.dating.lavada.databinding.FragmentAboutMyselfBinding
import com.uzlov.dating.lavada.domain.models.MALE
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject


class AboutMyselfFragment :
    BaseFragment<FragmentAboutMyselfBinding>(FragmentAboutMyselfBinding::inflate) {

    @Inject
    lateinit var usersRepository : IUsersRepository
    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService
    private var user = User()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            it.getParcelable<User>(NEW_USER)?.let { _user ->
                user = _user.copy()
            }
        }


        addTextChangedListener()
        initListeners()

    }

    private fun initListeners() {
        with(viewBinding) {
            toggleSex.addOnButtonCheckedListener { group, checkedId, isChecked ->
                if (checkedId == btnSexM.id) {
                    user.male = MALE.MAN
                    Toast.makeText(context, "Button1 Clicked", Toast.LENGTH_SHORT).show()
                } else if (checkedId == btnSexW.id) {
                    user.male = MALE.WOMAN
                    Toast.makeText(context, "Button2 Clicked", Toast.LENGTH_SHORT).show()
                } else if (checkedId == btnSexAnother.id) {
                    user.male = MALE.ANOTHER
                    Toast.makeText(context, "Button3 Clicked", Toast.LENGTH_SHORT).show()
                }
            }
            slAge.addOnChangeListener { _, value, _ ->
                user.age = value.toInt()
            }
            btnNext.setOnClickListener {
                user.name = tiEtName.text.toString()
                user.about = tiEtLocation.text.toString()
                user.uid = firebaseEmailAuthService.getUserUid()
                if (user.email.isNullOrBlank()){
                    user.email = firebaseEmailAuthService.auth.currentUser?.email
                }
                usersRepository.putUser(user)
                updateUI()
  //              Toast.makeText(context, "Ваш аккаунт создан. Переход на другой экран будет доступен с ближайшее время", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addTextChangedListener() {
        viewBinding.tiEtName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                verifyEditText()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        viewBinding.tiEtLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                verifyEditText()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun verifyEditText() {
        with(viewBinding) {
            btnNext.isEnabled = !tiEtName.text.isNullOrBlank() && !tiEtLocation.text.isNullOrBlank()
        }

    }

    private fun updateUI() {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, UploadVideoFragment.newInstance())
                .commit()

    }

    companion object {
        private const val NEW_USER = "user"

        fun newInstance() =
            AboutMyselfFragment().apply {
                arguments = Bundle().apply {
                }
            }

        fun newInstance(user: User?) = AboutMyselfFragment().apply {
            arguments = Bundle().apply {
                putParcelable(NEW_USER, user)
            }
        }
    }
}
