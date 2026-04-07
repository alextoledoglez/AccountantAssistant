package com.personal.accountantAssistant.ui.menu

import android.view.Menu
import android.view.MenuInflater
import androidx.compose.runtime.Composable
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.services.SignInService
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MenuFragment : BaseFragment() {

    private val viewModel: MenuViewModel by viewModel()
    private val signInService: SignInService? by inject()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.hideMenuOptions()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun initComponents() {
        setHasOptionsMenu(true)
    }

    override fun initObservers() {
        viewModel.loadUser()
    }

    @Composable
    override fun ScreenContent() {
        MenuScreen(
            viewModel = viewModel,
            onLogout = ::logOutConfirmation
        )
    }

    private fun logOutConfirmation() {
        AlertDialogBuilder(requireContext()).showConfirmationFrom(
            com.personal.accountantAssistant.R.string.logout_confirmation_title,
            com.personal.accountantAssistant.R.string.logout_confirmation_message,
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