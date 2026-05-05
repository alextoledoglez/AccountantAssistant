package com.personal.accountantAssistant.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr
import com.personal.accountantAssistant.ui.theme.Dimens

@Composable
fun HomeGridItem(item: DashboardItemModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(all = Dimens.spacingXs)
            .clickable(onClick = onClick),
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
                painter = painterResource(item.drawableRes),
                contentDescription = null,
                tint = Color(item.color),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.iconSize)
                    .padding(top = Dimens.spacingXs)
            )
            Text(
                text = stringResource(item.textRes),
                color = Color(item.color),
                fontSize = Dimens.textSm,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.spacingXs)
            )
            Text(
                text = item.value.abs().toCurrencyMaskedStr(),
                color = Color(item.color),
                fontSize = Dimens.textMd,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.spacingXs)
            )
        }
    }
}