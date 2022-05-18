package com.uzlov.dating.lavada.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.appComponent
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.databinding.FragmentShopBinding
import com.uzlov.dating.lavada.ui.fragments.dialogs.FragmentBuyCoins
import com.uzlov.dating.lavada.viemodels.UsersViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import javax.inject.Inject

class ShopFragment : BaseFragment<FragmentShopBinding>(FragmentShopBinding::inflate) {

    @Inject
    lateinit var firebaseEmailAuthService: FirebaseEmailAuthService

    @Inject
    lateinit var factoryViewModel: ViewModelFactory
    private lateinit var model: UsersViewModel

    private val shopFragmentListener: FragmentBuyCoins.OnSelectListener =
        object : FragmentBuyCoins.OnSelectListener {
            override fun coins300() {
                Toast.makeText(context, "Покупаем 300 монет", Toast.LENGTH_SHORT).show()
//            если покупка удалась
                if (true) {
                    firebaseEmailAuthService.getUser()?.getIdToken(true)
                        ?.addOnSuccessListener { tokenFb ->
                            lifecycleScope.launchWhenResumed {
                                model.authRemoteUser(hashMapOf("token" to tokenFb.token))
                                    .observe(viewLifecycleOwner) { tokenBack ->
                                        model.postRemoteBalance(
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
                                model.authRemoteUser(hashMapOf("token" to tokenFb.token))
                                    .observe(viewLifecycleOwner) { tokenBack ->
                                        model.postRemoteBalance(
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
                                model.authRemoteUser(hashMapOf("token" to tokenFb.token))
                                    .observe(viewLifecycleOwner) { tokenBack ->
                                        model.postRemoteBalance(
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
                                model.authRemoteUser(hashMapOf("token" to tokenFb.token))
                                    .observe(viewLifecycleOwner) { tokenBack ->
                                        model.postRemoteBalance(
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
        model = factoryViewModel.create(UsersViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        updateData()
        loadImage(resources.getDrawable(R.drawable.price_sale), viewBinding.ivSale)

    }

    private fun updateData() {
        firebaseEmailAuthService.getUser()?.getIdToken(true)?.addOnSuccessListener { tokenFb ->
            lifecycleScope.launchWhenResumed {
                model.authRemoteUser(hashMapOf("token" to tokenFb.token))
                    .observe(viewLifecycleOwner) { tokenBack ->
                        model.getRemoteBalance(tokenBack).observe(viewLifecycleOwner) { result ->
                            viewBinding.btnCoins.text = result.toString()
                        }
                        model.getUser(tokenBack).observe(viewLifecycleOwner){ reUser ->
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