package com.uzlov.dating.lavada.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.android.billingclient.api.*
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.databinding.FragmentAboutPremiumBinding
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentBuyPremium
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentSelectSourceVideo
import com.uzlov.dating.lavada.viemodels.SubscriptionsViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject

class AboutPremiumFragment :
    BaseFragment<FragmentAboutPremiumBinding>(FragmentAboutPremiumBinding::inflate) {

    @Inject
    lateinit var factoryViewModel: ViewModelFactory

    @Inject
    lateinit var billingClientBuilder: BillingClient.Builder

    private lateinit var subsViewModel: SubscriptionsViewModel

    private var billingClient: BillingClient? = null
    // callback about purchase!
    private val purchasesUpdateListener = PurchasesUpdatedListener { result, purchase ->
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                // успешная покупка, сохраняем её в бд
                purchase?.forEach {
                    Log.e(javaClass.simpleName, it.packageName)
                }
            }
            else -> {
                Toast.makeText(requireContext(), result.debugMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        billingClient = billingClientBuilder.setListener(purchasesUpdateListener).build()
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(p0: BillingResult) {

            }

            override fun onBillingServiceDisconnected() {

            }
        })
        subsViewModel = factoryViewModel.create(SubscriptionsViewModel::class.java)
        subsViewModel.getAllSubscriptions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadImage(resources.getDrawable(R.drawable.about_premium), viewBinding.ivAboutPremium)
        viewBinding.tbBackAction.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        viewBinding.btnBuyPremium.setOnClickListener {
            subsViewModel.checkSubscription()
        }

        observeData()
    }

    private fun observeData() {
        subsViewModel.hasSubsMoth.observe(viewLifecycleOwner){ hasSubscription ->
            if (hasSubscription){
                // has subs
            } else {
                // go to play market shop or show needed view
                subsViewModel.allSubs.observe(viewLifecycleOwner){
                    it?.firstOrNull()?.let { detail ->
                        val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(detail)
                            .build()
                        billingClient?.launchBillingFlow(requireActivity(), flowParams)
                    }

                }
            }
        }
    }

    companion object {
        fun newInstance(): AboutPremiumFragment {
            return AboutPremiumFragment()
        }
    }

    private fun loadImage(image: Drawable, container: ImageView) {
        view?.let {
            Glide
                .with(it.context)
                .load(image)
                .error(R.drawable.ic_default_user)
                .into(container)
        }
    }
}
