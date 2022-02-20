package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.databinding.FragmentChatsLayoutBinding
import com.uzlov.dating.lavada.ui.adapters.UsersChatsAdapter
import com.uzlov.dating.lavada.ui.adapters.UsersProfileStoriesAdapter

class ChatsTelegram : BaseFragment<FragmentChatsLayoutBinding>(FragmentChatsLayoutBinding::inflate) {

    private val storiesAdapter by lazy {
        UsersProfileStoriesAdapter()
    }

    private val chatAdapter by lazy {
        UsersChatsAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding){
            historyRecyclerView.adapter = storiesAdapter
            chatRecyclerView.adapter = chatAdapter
        }
    }

    fun initListeners(){
        with(viewBinding){
            tbBackAction.setOnClickListener {

            }

            tbSearchAction.setOnClickListener {

            }
        }
    }

    private fun setSearchState(visible: Int){

        when(visible){
            View.GONE -> {
                viewBinding.textLayoutSearch.visibility = visible
                viewBinding.cancelSearch.visibility = visible
                viewBinding.chatLabel.visibility = View.VISIBLE
                viewBinding.tbSearchAction.visibility = View.VISIBLE
            }
            View.VISIBLE-> {
                viewBinding.textLayoutSearch.visibility = visible
                viewBinding.cancelSearch.visibility = visible
                viewBinding.chatLabel.visibility = View.INVISIBLE
                viewBinding.tbSearchAction.visibility = View.INVISIBLE
            }
        }

    }
}