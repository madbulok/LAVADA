package com.uzlov.dating.lavada.ui.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.GiftsBottomSheetDialogFragmentBinding
import com.uzlov.dating.lavada.domain.models.CategoryGifts
import com.uzlov.dating.lavada.domain.models.Gift
import com.uzlov.dating.lavada.ui.activities.HostActivity
import com.uzlov.dating.lavada.ui.adapters.GiftsAdapter
import com.uzlov.dating.lavada.viemodels.GiftsViewModels
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject

class GiftsBottomSheetDialogFragment : BottomSheetDialogFragment() {
    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var factoryViewModel: ViewModelFactory
    private lateinit var model: UsersViewModel
    private lateinit var giftModel: GiftsViewModels

    private var viewBinding: GiftsBottomSheetDialogFragmentBinding? = null
    private var resultCut = CategoryGifts()

    private val giftsAdapter by lazy {
        GiftsAdapter(buyGiftCallback)
    }

    private val buyGiftCallback by lazy {
        object : GiftsAdapter.OnGiftsClickListener {
            override fun onClick(gift: Gift) {
                firebaseEmailAuthService.getUserUid()?.let { it ->
                    lifecycleScope.launchWhenResumed {
                        model.getUserSuspend(it)?.let { result ->
                            if (result.balance >= gift.cost){
                                val newBalance = result.balance - gift.cost
                                viewBinding?.btnCoins?.text = newBalance.toString()
                                model.updateUser(it, "balance", newBalance)
                                Toast.makeText(context, "отправляем подарок/оставляем себе/ что мы там тут с ним делаем", Toast.LENGTH_SHORT).show()
                            }
                            if (result.balance < gift.cost) {
                                viewBinding?.btnCoins?.setTextColor(resources.getColor(R.color.Error))
                                viewBinding?.tvNeedMoreCoins?.visibility = View.VISIBLE

                            } else {
                                viewBinding?.btnCoins?.setTextColor(resources.getColor(R.color.Primary_text))
                                viewBinding?.tvNeedMoreCoins?.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

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
        giftModel = factoryViewModel.create(GiftsViewModels::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = GiftsBottomSheetDialogFragmentBinding.inflate(layoutInflater, container, false).also {
        viewBinding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding?.rvGroup1?.adapter = giftsAdapter
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
                    viewBinding?.btnCoins?.text = result.balance.toString()
                    viewBinding?.btnCoins?.text = result.balance.toString()
                    //подставить тут актуальную минимальную цену или вообще тягать ее из цен на подарки
                    if (result.balance == 0) {
                        viewBinding?.btnCoins?.setTextColor(resources.getColor(R.color.Error))
                        viewBinding?.tvNeedMoreCoins?.visibility = View.VISIBLE

                    } else {
                        viewBinding?.btnCoins?.setTextColor(resources.getColor(R.color.Primary_text))
                        viewBinding?.tvNeedMoreCoins?.visibility = View.GONE
                    }
                }
            }
        }
        loadGifts()

        viewBinding?.btnTopUp?.setOnClickListener {
            (requireActivity() as HostActivity).startShopFragment()


        }
    }

    private fun loadGifts() {
        giftModel.getCategoryByID("gifts").observe(this, {
            if (it != null) {
                resultCut = it
                renderUi(
                    resultCut.gifts
                )
            }
        })
    }

    private fun renderUi(gifts: List<Gift>?) {
        if (!gifts.isNullOrEmpty()) {
            giftsAdapter.setGifts(gifts)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }
}