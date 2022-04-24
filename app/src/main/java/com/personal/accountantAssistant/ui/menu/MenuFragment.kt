package com.personal.accountantAssistant.ui.menu

import android.view.Menu
import android.view.MenuInflater
import com.bumptech.glide.Glide
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.databinding.FragmentMenuBinding
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.providers.AdProvider
import org.koin.android.ext.android.inject


class MenuFragment : BaseFragment<MenuViewModel>() {

    override val binding by viewBinding(FragmentMenuBinding::inflate)
    private val lytUser by lazy { binding.lytUser }
    private val lytContent by lazy { binding.lytContent }
    private val adProvider: AdProvider? by inject()

    override fun onDestroy() {
        super.onDestroy()
        adProvider?.destroyAd()
        lytContent.rvContent.destroyAdapter()
    }

    override fun onPause() {
        super.onPause()
        adProvider?.pauseAd()
    }

    override fun onResume() {
        super.onResume()
        adProvider?.resumeAd()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.hideMenuOptions()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun initComponents() {
        with(lytUser) { ibLogout.setOnClickListener { logOutConfirmation() } }
        //adProvider?.loadAdOn(binding.flAds)
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) {
                lytContent.srlContent.updateRefreshing(it.orFalse())
            }
            flipper.observe(viewLifecycleOwner) {
                lytContent.vfContent.updateDisplayedChild(it.ordinal)
            }
            user.observe(viewLifecycleOwner) { setupUserLayout(it) }
            getUser()
        }
    }

    private fun setupUserLayout(user: UserModel?) {
        with(lytUser) {
            Glide.with(requireContext()).load(user?.photoUri)
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .into(ivPhoto)
            tvUser.text = user?.name.orEmpty()
            tvEmail.text = user?.email.orEmpty()
        }
    }

    private fun logOutConfirmation() {
    }

    companion object {
        fun newInstance() = MenuFragment()
    }
}