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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.enums.FlipperViews
import com.personal.accountantAssistant.domain.models.MenuItemModel
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.extensions.orFalse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    viewModel: MenuViewModel,
    onLogout: () -> Unit
) {
    val isLoading by viewModel.isLoading.observeAsState(false)
    val flipper by viewModel.flipper.observeAsState()
    val user by viewModel.user.observeAsState()
    val isLoggedOut by viewModel.isLoggedOut.observeAsState(false)

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut.orFalse()) onLogout()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.backgroundColor))
    ) {
        UserDataSection(
            user = user,
            modifier = Modifier.padding(dimensionResource(R.dimen.default_material_margin))
        )

        when (flipper) {
            FlipperViews.LOADER -> Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = colorResource(R.color.primaryColor)) }

            else -> PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = { viewModel.loadUser() },
                modifier = Modifier.weight(1f)
            ) {
                Box(modifier = Modifier.fillMaxSize())
            }
        }

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = dimensionResource(R.dimen.default_material_margin))
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primaryColor))
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logout),
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
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
                    minimumWidth = 48.dp.value.toInt()
                    minimumHeight = 48.dp.value.toInt()
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
                .size(48.dp)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = dimensionResource(R.dimen.half_material_margin))
        ) {
            Text(
                text = user?.name.orEmpty().uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black
            )
            Text(
                text = user?.email.orEmpty(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MenuGridItem(item: MenuItemModel) {
    Card(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.small_material_margin)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.card_view_content_padding)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = null,
                tint = colorResource(item.color),
                modifier = Modifier
                    .size(48.dp)
                    .padding(vertical = dimensionResource(R.dimen.small_material_margin))
            )
            Text(
                text = stringResource(item.text),
                color = colorResource(item.color),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}