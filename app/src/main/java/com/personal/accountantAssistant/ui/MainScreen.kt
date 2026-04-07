package com.personal.accountantAssistant.ui

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.mappers.toBill
import com.personal.accountantAssistant.data.mappers.toBuy
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.providers.AdProvider
import com.personal.accountantAssistant.services.SignInService
import com.personal.accountantAssistant.ui.bills.BillsScreen
import com.personal.accountantAssistant.ui.bills.BillsViewModel
import com.personal.accountantAssistant.ui.buys.BuysScreen
import com.personal.accountantAssistant.ui.buys.BuysViewModel
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.ui.home.HomeScreen
import com.personal.accountantAssistant.ui.home.HomeViewModel
import com.personal.accountantAssistant.ui.login.LoginActivity
import com.personal.accountantAssistant.ui.menu.MenuScreen
import com.personal.accountantAssistant.ui.menu.MenuViewModel
import com.personal.accountantAssistant.ui.wallet.WalletDetailsFragment
import com.personal.accountantAssistant.ui.wallet.WalletScreen
import com.personal.accountantAssistant.ui.wallet.WalletViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    activity: FragmentActivity,
    homeViewModel: HomeViewModel,
    walletViewModel: WalletViewModel,
    buysViewModel: BuysViewModel,
    billsViewModel: BillsViewModel,
    menuViewModel: MenuViewModel,
    adProvider: AdProvider?,
    signInService: SignInService?
) {
    val tabs = TabPositions.entries
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    var currentTab by remember { mutableStateOf(TabPositions.HOME) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.settledPage) {
        currentTab = tabs[pagerState.settledPage]
    }

    LaunchedEffect(currentTab) {
        if (pagerState.currentPage != currentTab.position) {
            pagerState.animateScrollToPage(currentTab.position)
        }
    }

    LaunchedEffect(Unit) {
        homeViewModel.loadPeriodDates()
        homeViewModel.loadAvailableMoney()
        walletViewModel.loadCards()
        buysViewModel.loadBuys()
        billsViewModel.loadBills()
        menuViewModel.loadUser()
    }

    val hasDeleteAllAction = currentTab in listOf(TabPositions.WALLET, TabPositions.BUYS, TabPositions.BILLS)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(currentTab.titleRes)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (hasDeleteAllAction) {
                        IconButton(onClick = { showDeleteAllDialog = true }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete_white),
                                contentDescription = stringResource(R.string.delete_all_records_title)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                painter = painterResource(tab.iconRes),
                                contentDescription = stringResource(tab.titleRes)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            when (currentTab) {
                TabPositions.WALLET -> MainFab {
                    WalletDetailsFragment.showDialogFragment(
                        CardModel(), walletViewModel::saveCard, activity.supportFragmentManager
                    )
                }
                TabPositions.BUYS -> MainFab {
                    ExpenseDetailsFragment.showDialogFragment(
                        ExpenseModel().toBuy(), buysViewModel::saveBuy, activity.supportFragmentManager
                    )
                }
                TabPositions.BILLS -> MainFab {
                    ExpenseDetailsFragment.showDialogFragment(
                        ExpenseModel().toBill(), billsViewModel::saveBill, activity.supportFragmentManager
                    )
                }
                else -> {}
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            beyondViewportPageCount = 0
        ) { page ->
            when (tabs[page]) {
                TabPositions.HOME -> HomeScreen(
                    viewModel = homeViewModel,
                    adProvider = adProvider,
                    onShowDatePicker = {
                        MaterialDatePicker.Builder.dateRangePicker()
                            .setTitleText(activity.getString(R.string.select_period))
                            .setSelection(homeViewModel.getSelectedPeriod())
                            .build()
                            .apply {
                                addOnPositiveButtonClickListener { period ->
                                    homeViewModel.savePeriodDates(period)
                                    homeViewModel.loadPeriodDates()
                                    homeViewModel.loadAvailableMoney()
                                }
                            }
                            .show(activity.supportFragmentManager, String.EMPTY)
                    },
                    onItemClick = { drawableRes ->
                        currentTab = when (drawableRes) {
                            R.drawable.ic_buys -> TabPositions.BUYS
                            R.drawable.ic_bills -> TabPositions.BILLS
                            else -> TabPositions.WALLET
                        }
                    }
                )
                TabPositions.WALLET -> WalletScreen(
                    viewModel = walletViewModel,
                    onEdit = { card ->
                        WalletDetailsFragment.showDialogFragment(
                            card, walletViewModel::saveCard, activity.supportFragmentManager
                        )
                    },
                    onActive = walletViewModel::switchActiveCard,
                    onDelete = walletViewModel::deleteCard
                )
                TabPositions.BUYS -> BuysScreen(
                    viewModel = buysViewModel,
                    onEdit = { model ->
                        ExpenseDetailsFragment.showDialogFragment(
                            model, buysViewModel::saveBuy, activity.supportFragmentManager
                        )
                    },
                    onActive = buysViewModel::switchActiveBuy,
                    onDelete = buysViewModel::deleteBuy
                )
                TabPositions.BILLS -> BillsScreen(
                    viewModel = billsViewModel,
                    onEdit = { model ->
                        ExpenseDetailsFragment.showDialogFragment(
                            model, billsViewModel::saveBill, activity.supportFragmentManager
                        )
                    },
                    onActive = billsViewModel::switchActiveBill,
                    onDelete = billsViewModel::deleteBill
                )
                TabPositions.PROFILE -> MenuScreen(
                    viewModel = menuViewModel,
                    onLogoutClick = { signInService?.signOut(menuViewModel::clearUser) },
                    onLoggedOut = {
                        Intent(activity, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            activity.startActivity(this)
                        }
                    }
                )
            }
        }

        if (showDeleteAllDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAllDialog = false },
                title = { Text(stringResource(R.string.delete_all_records_title)) },
                text = { Text(stringResource(R.string.delete_all_records_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteAllDialog = false
                        when (currentTab) {
                            TabPositions.WALLET -> walletViewModel.deleteAllCards()
                            TabPositions.BUYS -> buysViewModel.deleteAllBuys()
                            TabPositions.BILLS -> billsViewModel.deleteAllBills()
                            else -> {}
                        }
                    }) { Text(stringResource(R.string.ok)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAllDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
private fun MainFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add_white),
            contentDescription = stringResource(R.string.add_action),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}