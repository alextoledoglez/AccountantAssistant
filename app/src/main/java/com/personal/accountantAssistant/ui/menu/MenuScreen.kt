package com.personal.accountantAssistant.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.personal.accountantAssistant.BuildConfig
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.enums.FlipperViews
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.services.SignInService
import com.personal.accountantAssistant.ui.login.LoginActivity
import com.personal.accountantAssistant.ui.menu.components.UserDataSection
import com.personal.accountantAssistant.ui.theme.Dimens
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen() {
    val context = LocalContext.current
    val viewModel: MenuViewModel = koinViewModel()
    val signInService: SignInService? = koinInject()

    val isLoading by viewModel.isLoading.observeAsState(false)
    val flipper by viewModel.flipper.observeAsState()
    val user by viewModel.user.observeAsState()
    val isLoggedOut by viewModel.isLoggedOut.observeAsState(false)

    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadUser() }

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut.orFalse()) LoginActivity.startActivity(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        UserDataSection(user = user, modifier = Modifier.padding(Dimens.spacingMd))

        when (flipper) {
            FlipperViews.LOADER -> Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }

            else -> PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = { viewModel.loadUser() },
                modifier = Modifier.weight(1f)
            ) {
                Box(modifier = Modifier.fillMaxSize())
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text(stringResource(R.string.logout_confirmation_title)) },
                text = { Text(stringResource(R.string.logout_confirmation_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        signInService?.signOut(viewModel::clearUser)
                    }) { Text(stringResource(R.string.ok)) }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        Text(
            text = stringResource(R.string.app_version, BuildConfig.VERSION_NAME),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXs),
            textAlign = TextAlign.Center,
            fontSize = Dimens.textMd
        )

        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = Dimens.spacingMd)
                .height(Dimens.iconSize),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logout),
                contentDescription = null,
                modifier = Modifier.padding(end = Dimens.spacingSm)
            )
            Text(text = stringResource(R.string.logout_action))
        }
    }
}