package com.personal.accountantAssistant.ui.common

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun AdaptiveScreen(portrait: @Composable () -> Unit, landscape: @Composable () -> Unit) {
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    if (isPortrait) portrait() else landscape()
}