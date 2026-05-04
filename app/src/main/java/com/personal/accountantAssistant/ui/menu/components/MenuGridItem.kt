package com.personal.accountantAssistant.ui.menu.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.personal.accountantAssistant.domain.models.MenuItemModel
import com.personal.accountantAssistant.ui.theme.Dimens


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