package com.uzlov.dating.lavada.ui.fragments.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentBlackListBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.ui.adapters.BlackListAdapter
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import javax.inject.Inject

class BlackListFragment :
    BaseFragment<FragmentBlackListBinding>(FragmentBlackListBinding::inflate) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var model: UsersViewModel

    @Inject
    lateinit var auth: FirebaseEmailAuthService

    private var self = User()

    private val blackListAdapter by lazy {
        BlackListAdapter(openChatCallback)
    }

    private val openChatCallback by lazy {
        object : BlackListAdapter.OnBlackListClickListener {
            override fun onClick(blackList: User) {
                val removeUID = blackList.uid
                self.black_list.remove(removeUID)
                model.updateUser(self.uid, "black_list", self.black_list)
                loadBlockedUsers()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        model = viewModelFactory.create(UsersViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.tbBackAction.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        viewBinding.blackListRecyclerView.adapter = blackListAdapter
        loadBlockedUsers()
    }

    private fun loadBlockedUsers() {
        model.getUsers()?.observe(this, { users ->
            auth.getUserUid()?.let { it ->
                lifecycleScope.launchWhenResumed {
                    model.getUserSuspend(it)?.let { result ->
                        result.let {
                            if (it != null) {
                                self = it
                            }
                            renderUi(
                                model.blockedUsers(
                                    users, self.black_list
                                )
                            )
                        }
                    }
                }
            }
        })
    }

    private fun renderUi(users: List<User>?) {
        if (!users.isNullOrEmpty()) {
            blackListAdapter.setBlackList(users)
        } else {
            viewBinding.blackListRecyclerView.visibility = View.GONE
            Toast.makeText(requireContext(), "В списке ЧС пока никого нет!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        fun newInstance() =
            BlackListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}