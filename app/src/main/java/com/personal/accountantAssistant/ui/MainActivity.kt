package com.personal.accountantAssistant.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.personal.accountantAssistant.extensions.closeApp
import com.personal.accountantAssistant.providers.AdProvider
import com.personal.accountantAssistant.services.SignInService
import com.personal.accountantAssistant.ui.bills.BillsViewModel
import com.personal.accountantAssistant.ui.buys.BuysViewModel
import com.personal.accountantAssistant.ui.home.HomeViewModel
import com.personal.accountantAssistant.ui.menu.MenuViewModel
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import com.personal.accountantAssistant.ui.wallet.WalletViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val homeViewModel: HomeViewModel by viewModel()
    private val walletViewModel: WalletViewModel by viewModel()
    private val buysViewModel: BuysViewModel by viewModel()
    private val billsViewModel: BillsViewModel by viewModel()
    private val menuViewModel: MenuViewModel by viewModel()

    private val adProvider: AdProvider? by inject()
    private val signInService: SignInService? by inject()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            closeApp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        setContent {
            AccountantTheme {
                MainScreen(
                    activity = this,
                    homeViewModel = homeViewModel,
                    walletViewModel = walletViewModel,
                    buysViewModel = buysViewModel,
                    billsViewModel = billsViewModel,
                    menuViewModel = menuViewModel,
                    adProvider = adProvider,
                    signInService = signInService
                )
            }
        }
    }
}