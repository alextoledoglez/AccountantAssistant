package com.personal.accountantAssistant.ui

import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.ui.bills.BillsScreen
import com.personal.accountantAssistant.ui.bills.BillsViewModel
import com.personal.accountantAssistant.ui.buys.BuysScreen
import com.personal.accountantAssistant.ui.buys.BuysViewModel
import com.personal.accountantAssistant.ui.home.HomeScreen
import com.personal.accountantAssistant.ui.menu.MenuScreen
import com.personal.accountantAssistant.ui.common.PrimaryFabButton
import com.personal.accountantAssistant.ui.scanner.launchScannerActivity
import com.personal.accountantAssistant.ui.scanner.onScannerActivityResult
import com.personal.accountantAssistant.ui.wallet.WalletScreen
import com.personal.accountantAssistant.ui.wallet.WalletViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun MainScreen() {
    val context = LocalContext.current
    val fragmentManager = (LocalActivity.current as? FragmentActivity)?.supportFragmentManager
    val coroutineScope = rememberCoroutineScope()

    val mainViewModel: MainViewModel = koinViewModel()
    val walletViewModel: WalletViewModel = koinViewModel()
    val buysViewModel: BuysViewModel = koinViewModel()
    val billsViewModel: BillsViewModel = koinViewModel()

    val pagerState = rememberPagerState(pageCount = { TabPositions.entries.size })
    val currentTab = TabPositions.entries[pagerState.settledPage]
    val showDeleteAllDialog by mainViewModel.showDeleteAllDialog.collectAsState()

    val navigateTo: (TabPositions) -> Unit = { tab ->
        if (pagerState.currentPage != tab.position) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(tab.position)
            }
        }
    }

    val deleteAllByTab = remember(walletViewModel, buysViewModel, billsViewModel) {
        mapOf(
            TabPositions.WALLET to walletViewModel::deleteAllCards,
            TabPositions.BUYS to buysViewModel::deleteAllBuys,
            TabPositions.BILLS to billsViewModel::deleteAllBills
        )
    }

    val scanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        fragmentManager?.onScannerActivityResult(
            currentTab = currentTab,
            activityResult = activityResult,
            walletViewModel = walletViewModel,
            buysViewModel = buysViewModel,
            billsViewModel = billsViewModel
        )
    }

    Scaffold(
        topBar = {
            MainTopBar(
                currentTab = currentTab,
                hasDeleteAllAction = currentTab.hasDeleteAll,
                onDeleteAllClick = mainViewModel::showDeleteDialog
            )
        },
        bottomBar = {
            MainBottomBar(
                currentTab = currentTab,
                onTabSelected = navigateTo
            )
        },
        floatingActionButton = {
            PrimaryFabButton(
                isVisible = currentTab.scanMode != null,
                painterResourceId = R.drawable.ic_scan_white,
                stringResourceId = R.string.scan_action,
                onClick = { scanLauncher.launchScannerActivity(context, currentTab.scanMode) }
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            beyondViewportPageCount = 1
        ) { page ->
            when (TabPositions.entries[page]) {
                TabPositions.HOME -> HomeScreen(navigateTo)
                TabPositions.WALLET -> WalletScreen()
                TabPositions.BUYS -> BuysScreen()
                TabPositions.BILLS -> BillsScreen()
                TabPositions.PROFILE -> MenuScreen()
            }
        }

        if (showDeleteAllDialog) {
            DeleteAllDialog(
                onConfirm = { deleteAllByTab[currentTab]?.invoke() },
                onDismiss = mainViewModel::dismissDeleteDialog
            )
        }
    }
}