package com.uzlov.dating.lavada.ui.fragments.settings

import android.os.Bundle
import android.view.View
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import com.uzlov.dating.lavada.databinding.FragmentNotificationsBinding
import com.uzlov.dating.lavada.domain.models.NotificationsFilter
import com.uzlov.dating.lavada.ui.fragments.BaseFragment
import javax.inject.Inject

class NotificationsFragment :
    BaseFragment<FragmentNotificationsBinding>(FragmentNotificationsBinding::inflate) {

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    private var notificationsFilter = NotificationsFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        notificationsFilter = preferenceRepository.readNotificationsFilter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        with(viewBinding) {
            swMessages.isChecked = notificationsFilter.messages
            swMatches.isChecked = notificationsFilter.matches
            swLikes.isChecked = notificationsFilter.likes
            swGifts.isChecked = notificationsFilter.gifts
            swWatchingVideo.isChecked = notificationsFilter.watchingVideo
            swNews.isChecked = notificationsFilter.news
        }
    }

    private fun initListeners() {
        with(viewBinding) {
            tbBackAction.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            swMessages.setOnCheckedChangeListener { _, isChecked ->
                notificationsFilter.messages = isChecked
                preferenceRepository.updateNotificationsFilter(notificationsFilter)
            }
            swMatches.setOnCheckedChangeListener { _, isChecked ->
                notificationsFilter.matches = isChecked
                preferenceRepository.updateNotificationsFilter(notificationsFilter)
            }
            swLikes.setOnCheckedChangeListener { _, isChecked ->
                notificationsFilter.likes = isChecked
                preferenceRepository.updateNotificationsFilter(notificationsFilter)
            }
            swWatchingVideo.setOnCheckedChangeListener { _, isChecked ->
                notificationsFilter.watchingVideo = isChecked
                preferenceRepository.updateNotificationsFilter(notificationsFilter)
            }
            swGifts.setOnCheckedChangeListener { _, isChecked ->
                notificationsFilter.gifts = isChecked
                preferenceRepository.updateNotificationsFilter(notificationsFilter)
            }
            swNews.setOnCheckedChangeListener { _, isChecked ->
                notificationsFilter.news= isChecked
                preferenceRepository.updateNotificationsFilter(notificationsFilter)
            }
        }


    }

    companion object {
        fun newInstance() =
            NotificationsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}