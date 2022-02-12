package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.databinding.MainVideosFragmentBinding
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.ui.SingleSnap
import com.uzlov.dating.lavada.ui.adapters.SingleVideoAdapter


class MainVideosFragment :
    BaseFragment<MainVideosFragmentBinding>(MainVideosFragmentBinding::inflate) {

    private val callback = object : SingleSnap.OnScrollListener {
        override fun onNext() {
            // load next profile
        }

        override fun onPrevious() {
            // restore cached previous profile
        }
    }

    private val snapHelper: SingleSnap = SingleSnap(callback)
    private val singleVideoAdapter = SingleVideoAdapter()
    private val testData = listOf<User>(
        User(),
        User(),
        User(),
        User(),
        User(),
        User(),
        User(),
        User(),
        User(),
        User(),
    )



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding) {
            rvVideosUsers.adapter = singleVideoAdapter
            snapHelper.attachToRecyclerView(rvVideosUsers)
        }

        singleVideoAdapter.setVideos(testData)
    }
}