package com.personal.accountantAssistant.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.ui.common.IconActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainTopBar(
    currentTab: TabPositions,
    hasDeleteAllAction: Boolean,
    onDeleteAllClick: () -> Unit
) {
    TopAppBar(
        title = {
            AnimatedContent(targetState = currentTab.titleRes) { titleResId ->
                Text(stringResource(titleResId))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            IconActionButton(
                isVisible = hasDeleteAllAction,
                painterResourceId = R.drawable.ic_delete_white,
                stringResourceId = R.string.delete_all_records_title,
                onClick = onDeleteAllClick
            )
        }
    )
}

@Composable
internal fun MainBottomBar(
    currentTab: TabPositions,
    onTabSelected: (TabPositions) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
        TabPositions.entries.forEach { tab ->
            NavigationBarItem(
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
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
}

@Composable
internal fun DeleteAllDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_all_records_title)) },
        text = { Text(stringResource(R.string.delete_all_records_message)) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) { Text(stringResource(R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}