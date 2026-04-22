package com.personal.accountantAssistant.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.personal.accountantAssistant.ui.theme.Dimens

@Composable
fun CircleIconButton(
    imageVector: ImageVector,
    @StringRes stringResourceId: Int,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(Dimens.iconSize)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = stringResource(stringResourceId),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}