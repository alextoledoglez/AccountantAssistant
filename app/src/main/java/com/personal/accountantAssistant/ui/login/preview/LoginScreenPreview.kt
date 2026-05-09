package com.personal.accountantAssistant.ui.login.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.personal.accountantAssistant.ui.login.LoginScreen
import com.personal.accountantAssistant.ui.theme.AccountantTheme

@PreviewScreenSizes
@PreviewLightDark
@Composable
fun LoginScreenPreview() {
    AccountantTheme { LoginPreviewContent() }
}

@Composable
internal fun LoginPreviewContent() {
    LoginScreen(isProcessing = false, isSignInVisible = true, onSignInClick = {})
}