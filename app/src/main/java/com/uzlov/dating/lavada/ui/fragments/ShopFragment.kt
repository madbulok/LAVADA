package com.uzlov.dating.lavada.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.Extensions
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentShopBinding
import com.uzlov.dating.lavada.ui.adapters.CoinsBoxAdapter
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentBuyCoins
import com.uzlov.dating.lavada.viemodels.PurchasesViewModel
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject

class ShopFragment : BaseFragment<FragmentShopBinding>(FragmentShopBinding::inflate) {

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var billingClientBuilder: BillingClient.Builder

    @Inject
    lateinit var factoryViewModel: ViewModelFactory
    private lateinit var usersViewModel: UsersViewModel
    private lateinit var purchasesViewModel: PurchasesViewModel

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

    private val onBoxClickListener = object : CoinsBoxAdapter.OnCoinBoxClickListener {
        override fun onClick(gift: SkuDetails) {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(gift)
                .build()
            billingClient?.launchBillingFlow(requireActivity(), flowParams)
        }
    }

    private val coinBoxAdapters by lazy {
        CoinsBoxAdapter(onBoxClickListener)
    }

    private val shopFragmentListener: FragmentBuyCoins.OnSelectListener =
        object : FragmentBuyCoins.OnSelectListener {
            override fun coins300() {
                Toast.makeText(context, "Покупаем 300 монет", Toast.LENGTH_SHORT).show()
//            если покупка удалась
                if (true) {
                    firebaseEmailAuthService.getUser()?.getIdToken(true)
                        ?.addOnSuccessListener { tokenFb ->
                            lifecycleScope.launchWhenResumed {
                                usersViewModel.authRemoteUser(hashMapOf("token" to tokenFb.token))
                                    .observe(viewLifecycleOwner) { tokenBack ->
                                        usersViewModel.postRemoteBalance(
                                            tokenBack,
                                            300.toString()
                                        )
                                    }
                            }
                        }
                }
                updateData()
            }

            override fun coins500() {
                Toast.makeText(context, "Покупаем 500 монет", Toast.LENGTH_SHORT).show()
                if (true) {
                    firebaseEmailAuthService.getUser()?.getIdToken(true)
                        ?.addOnSuccessListener { tokenFb ->
                            lifecycleScope.launchWhenResumed {
                                usersViewModel.authRemoteUser(hashMapOf("token" to tokenFb.token))
                                    .observe(viewLifecycleOwner) { tokenBack ->
                                        usersViewModel.postRemoteBalance(
                                            tokenBack,
                                            500.toString()
                                        )
                                    }
                            }
                        }
                }
                updateData()
            }

            override fun coins1500() {
                Toast.makeText(context, "Покупаем 1500 монет", Toast.LENGTH_SHORT).show()
                if (true) {
                    firebaseEmailAuthService.getUser()?.getIdToken(true)
                        ?.addOnSuccessListener { tokenFb ->
                            lifecycleScope.launchWhenResumed {
                                usersViewModel.authRemoteUser(hashMapOf("token" to tokenFb.token))
                                    .observe(viewLifecycleOwner) { tokenBack ->
                                        usersViewModel.postRemoteBalance(
                                            tokenBack,
                                            1500.toString()
                                        )
                                    }
                            }
                        }
                }
                updateData()
            }

            override fun coins3500() {
                Toast.makeText(context, "Покупаем 3500 монет", Toast.LENGTH_SHORT).show()
                if (true) {
                    firebaseEmailAuthService.getUser()?.getIdToken(true)
                        ?.addOnSuccessListener { tokenFb ->
                            lifecycleScope.launchWhenResumed {
                                usersViewModel.authRemoteUser(hashMapOf("token" to tokenFb.token))
                                    .observe(viewLifecycleOwner) { tokenBack ->
                                        usersViewModel.postRemoteBalance(
                                            tokenBack,
                                            3500.toString()
                                        )
                                    }
                            }
                        }
                }
                updateData()
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        billingClient = billingClientBuilder.setListener(purchasesUpdateListener).build()
        usersViewModel = factoryViewModel.create(UsersViewModel::class.java)
        purchasesViewModel = factoryViewModel.create(PurchasesViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        updateData()

=======
        loadLavCoinsBoxes()
        loadImage(resources.getDrawable(R.drawable.price_sale), viewBinding.ivSale)
        observeDataCoins()

    }

    private fun loadLavCoinsBoxes() {
        purchasesViewModel.getAllPurchases()
    }

    private fun observeDataCoins(){
        purchasesViewModel.allPurchases.observe(viewLifecycleOwner){ result ->
            setupSuperBox(result.firstOrNull { it.sku == "lavcoins_5000" })
            setupOtherBox(result.filter { it.sku != "lavcoins_5000" })
        }
    }

    private fun setupOtherBox(skuList: List<SkuDetails>) {
        coinBoxAdapters.setCoinBoxs(skuList.sortedBy {
            it.priceAmountMicros
        })
    }

    private fun setupSuperBox(superBox: SkuDetails?) {
        if (superBox != null){
            viewBinding.cardSuperBox.visibility = View.VISIBLE
            Glide.with(requireContext())
                .load(Extensions.getPictureForPurchase(superBox.sku))
                .into(viewBinding.ivScb)
            viewBinding
        } else {
            viewBinding.cardSuperBox.visibility = View.GONE
        }

    }

    private fun updateData() {
        firebaseEmailAuthService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
            lifecycleScope.launchWhenResumed {
                usersViewModel.authRemoteUser(hashMapOf("token" to tokenFb.token))
                    .observe(viewLifecycleOwner) { tokenBack ->
                        usersViewModel.getRemoteBalance(tokenBack).observe(viewLifecycleOwner) { result ->
                            viewBinding.btnCoins.text = result.toString()
                        }
                        usersViewModel.getUser(tokenBack).observe(viewLifecycleOwner){ reUser ->
                            if (reUser!!.premium){
                                viewBinding.cardBuyPremium.visibility = View.GONE
                                viewBinding.cardHasPremium.visibility = View.VISIBLE
                            } else{
                                viewBinding.cardBuyPremium.visibility = View.VISIBLE
                                viewBinding.cardHasPremium.visibility = View.GONE
                            }
                        }
                    }
            }
        }
    }

    private fun initListeners() {
        with(viewBinding) {
            rvCoinBox.adapter = coinBoxAdapters
            rvCoinBox.layoutManager = GridLayoutManager(requireContext(), 2)

            tvAboutPremiumLabel.setOnClickListener {
                parentFragmentManager.apply {
                    beginTransaction()
                        .replace(R.id.container, AboutPremiumFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
            tbBackAction.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
//            btnBuyCoins.setOnClickListener {
//                val buyCoinsFragment = FragmentBuyCoins(shopFragmentListener)
//                buyCoinsFragment.show(childFragmentManager, buyCoinsFragment.javaClass.simpleName)
//            }
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

    companion object {
        fun newInstance(): ShopFragment {
            val fragment = ShopFragment().apply {
            }
            return fragment
        }
    }
}