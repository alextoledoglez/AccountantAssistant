package com.personal.accountantAssistant.ui.menu.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.ui.common.MainBottomBar
import com.personal.accountantAssistant.ui.common.MainTopBar
import com.personal.accountantAssistant.ui.menu.UserDataSection
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import com.personal.accountantAssistant.ui.theme.Dimens

@Composable
internal fun MenuPreviewContent() {
    val mockUser = UserModel(
        name = "John Doe",
        email = "john.doe@gmail.com"
    )

    Scaffold(
        topBar = {
            MainTopBar(
                currentTab = TabPositions.PROFILE,
                hasDeleteAllAction = false,
                onDeleteAllClick = {}
            )
        },
        bottomBar = { MainBottomBar(currentTab = TabPositions.PROFILE, onTabSelected = {}) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            UserDataSection(
                user = mockUser,
                modifier = Modifier.padding(Dimens.spacingMd)
            )

            Box(modifier = Modifier.weight(1f).fillMaxSize())

            Button(
                onClick = {},
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
}

@Preview(showBackground = true, device = Devices.PIXEL_9_PRO)
@Composable
fun MenuScreenPreview() {
    AccountantTheme { MenuPreviewContent() }
}