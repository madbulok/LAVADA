package com.uzlov.dating.lavada.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.GiftsBottomSheetDialogFragmentBinding
import com.uzlov.dating.lavada.ui.activities.HostActivity
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject

class GiftsBottomSheetDialogFragment : BottomSheetDialogFragment() {
    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var factoryViewModel: ViewModelFactory
    private lateinit var model: UsersViewModel

    private var _viewBinding: GiftsBottomSheetDialogFragmentBinding? = null
    private val viewBinding get() = _viewBinding!!

    companion object {

        const val TAG = "CustomBottomSheetDialogFragment"
        fun newInstance(): GiftsBottomSheetDialogFragment {
            val fragment = GiftsBottomSheetDialogFragment().apply {
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        requireContext().appComponent.inject(this)
        model = factoryViewModel.create(UsersViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = GiftsBottomSheetDialogFragmentBinding.inflate(layoutInflater, container, false).also {
        _viewBinding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.let {
            val bottomSheet =
                it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.peekHeight = 370
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        firebaseEmailAuthService.getUserUid()?.let { it ->
            lifecycleScope.launchWhenResumed {
                model.getUserSuspend(it)?.let { result ->
                    viewBinding.btnCoins.text = result.balance.toString()
                    //подставить тут актуальную минимальную цену или вообще тягать ее из цен на подарки
                    if (result.balance < 100){
                        viewBinding.btnCoins.setTextColor(resources.getColor(R.color.Error))
                        viewBinding.tvNeedMoreCoins.visibility = View.VISIBLE
                    } else {
                        viewBinding.btnCoins.setTextColor(resources.getColor(R.color.Primary_text))
                        viewBinding.tvNeedMoreCoins.visibility = View.GONE
                    }
                }
            }
        }
        with(viewBinding) {
            btnTopUp.setOnClickListener {
                (requireActivity() as HostActivity).startShopFragment()
            }

        }
    }
}