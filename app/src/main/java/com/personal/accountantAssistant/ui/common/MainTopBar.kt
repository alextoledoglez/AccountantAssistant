package com.personal.accountantAssistant.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.enums.TabPositions

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