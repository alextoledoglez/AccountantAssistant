package com.personal.accountantAssistant.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.personal.accountantAssistant.ui.theme.AccountantTheme

@Composable
internal fun LoginPreviewContent() {
    LoginScreen(
        isProcessing = false,
        isSignInVisible = true,
        onSignInClick = {}
    )
}

@Preview(showBackground = true, device = Devices.PIXEL_9_PRO)
@Composable
fun LoginScreenPreview() {
    AccountantTheme { LoginPreviewContent() }
}