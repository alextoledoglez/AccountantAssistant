package com.personal.accountantAssistant.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.ui.bills.BillsScreen
import com.personal.accountantAssistant.ui.buys.BuysScreen
import com.personal.accountantAssistant.ui.common.DeleteAllDialog
import com.personal.accountantAssistant.ui.common.MainBottomBar
import com.personal.accountantAssistant.ui.common.MainTopBar
import com.personal.accountantAssistant.ui.common.MainNavHost
import com.personal.accountantAssistant.ui.home.HomeScreen
import com.personal.accountantAssistant.ui.menu.MenuScreen
import com.personal.accountantAssistant.ui.wallet.WalletScreen
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun MainScreen() {
    val mainViewModel: MainViewModel = koinViewModel()

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentTab = TabPositions.entries
        .find { it.route == backStackEntry?.destination?.route }
        ?: TabPositions.HOME

    val showDeleteAllDialog by mainViewModel.showDeleteAllDialog.collectAsState()

    var deleteAllAction: (() -> Unit)? by remember { mutableStateOf(null) }
    var fabSlot: (@Composable () -> Unit)? by remember { mutableStateOf(null) }

    LaunchedEffect(currentTab) {
        if (currentTab == TabPositions.HOME || currentTab == TabPositions.PROFILE) {
            fabSlot = null
            deleteAllAction = null
        }
    }

    val navigateTo: (TabPositions) -> Unit = { tab ->
        navController.navigate(tab.route) {
            popUpTo(TabPositions.HOME.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        topBar = {
            MainTopBar(
                currentTab = currentTab,
                hasDeleteAllAction = deleteAllAction != null,
                onDeleteAllClick = mainViewModel::showDeleteDialog
            )
        },
        bottomBar = { MainBottomBar(currentTab = currentTab, onTabSelected = navigateTo) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabSlot != null,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                fabSlot?.invoke()
            }
        }
    ) { paddingValues ->
        MainNavHost(paddingValues, navController, TabPositions.entries) {
            composable(TabPositions.HOME.route) { HomeScreen(navigateTo) }
            composable(TabPositions.WALLET.route) {
                WalletScreen(
                    onSetFab = { fabSlot = it },
                    onSetDeleteAll = { deleteAllAction = it }
                )
            }
            composable(TabPositions.BUYS.route) {
                BuysScreen(
                    onSetFab = { fabSlot = it },
                    onSetDeleteAll = { deleteAllAction = it }
                )
            }
            composable(TabPositions.BILLS.route) {
                BillsScreen(
                    onSetFab = { fabSlot = it },
                    onSetDeleteAll = { deleteAllAction = it }
                )
            }
            composable(TabPositions.PROFILE.route) { MenuScreen() }
        }

        if (showDeleteAllDialog) {
            DeleteAllDialog(
                onConfirm = { deleteAllAction?.invoke() },
                onDismiss = mainViewModel::dismissDeleteDialog
            )
        }
    }
}