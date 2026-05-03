package com.personal.accountantAssistant.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.personal.accountantAssistant.ui.theme.Dimens

@Composable
fun IconActionButton(
    isVisible: Boolean,
    @DrawableRes painterResourceId: Int,
    @StringRes stringResourceId: Int,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.size(Dimens.iconSize)) {
        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            IconButton(onClick = onClick) {
                Icon(
                    painter = painterResource(painterResourceId),
                    contentDescription = stringResource(stringResourceId)
                )
            }
        }
    }
}