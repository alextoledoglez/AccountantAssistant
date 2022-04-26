package com.personal.accountantAssistant.ui.menu

import android.view.Menu
import android.view.MenuInflater
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.adapters.MenuListAdapter
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.databinding.FragmentMenuBinding
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.services.SignInService
import org.koin.android.ext.android.inject

class MenuFragment : BaseFragment<MenuViewModel>() {

    override val binding by viewBinding(FragmentMenuBinding::inflate)
    private val signInService: SignInService? by inject()
    private val lytUser by lazy { binding.lytUser }
    private val lytContent by lazy { binding.lytContent }
    private val adapter by lazy { MenuListAdapter() }

    override fun onDestroy() {
        super.onDestroy()
        lytContent.rvContent.destroyAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.hideMenuOptions()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun initComponents() {
        with(lytContent) {
            srlContent.setOnRefreshListener { viewModel.loadUser() }
            rvContent.setGridLayoutAdapter(adapter, spanCount = 2)
        }
        binding.ibLogout.setOnClickListener { logOutConfirmation() }
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
            isLoggedOut.observe(viewLifecycleOwner) { if (it.orFalse()) activity?.closeApp() }
            loadUser()
        }
    }

    private fun setupUserLayout(user: UserModel?) {
        with(lytUser) {
            Glide.with(requireContext()).load(user?.photoPath?.toUri())
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .into(ivPhoto)
            tvUser.text = user?.name.orEmpty()
            tvEmail.text = user?.email.orEmpty()
        }
    }

    private fun logOutConfirmation() {
        AlertDialogBuilder(requireContext()).showConfirmationFrom(
            R.string.logout_confirmation_title,
            R.string.logout_confirmation_message,
            ::logOut
        ) {}
    }

    private fun logOut() {
        signInService?.signOut(viewModel::clearUser)
    }

    companion object {
        fun newInstance() = MenuFragment()
    }
}