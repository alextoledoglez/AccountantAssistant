package com.personal.accountantAssistant.ui.menu

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.enums.FlipperViews
import com.personal.accountantAssistant.domain.models.MenuItemModel
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.services.SignInService
import com.personal.accountantAssistant.ui.login.LoginActivity
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
        UserDataSection(
            user = user,
            modifier = Modifier.padding(Dimens.spacingMd)
        )

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

        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth(0.9f)
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

@Composable
fun UserDataSection(user: UserModel?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AndroidView(
            factory = { ctx ->
                ImageView(ctx).apply {
                    minimumWidth = Dimens.iconSize.value.toInt()
                    minimumHeight = Dimens.iconSize.value.toInt()
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            },
            update = { imageView ->
                Glide.with(context)
                    .load(user?.photoPath?.toUri())
                    .placeholder(R.drawable.ic_account)
                    .error(R.drawable.ic_account)
                    .circleCrop()
                    .into(imageView)
            },
            modifier = Modifier
                .size(Dimens.iconSize)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Dimens.spacingSm)
        ) {
            Text(
                text = user?.name.orEmpty().uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = Dimens.textMd,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = user?.email.orEmpty(),
                fontSize = Dimens.textMd,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MenuGridItem(item: MenuItemModel) {
    Card(
        modifier = Modifier.padding(Dimens.spacingXs),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.cardContentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = null,
                tint = colorResource(item.color),
                modifier = Modifier
                    .size(Dimens.iconSize)
                    .padding(vertical = Dimens.spacingXs)
            )
            Text(
                text = stringResource(item.text),
                color = colorResource(item.color),
                fontSize = Dimens.textSm,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}