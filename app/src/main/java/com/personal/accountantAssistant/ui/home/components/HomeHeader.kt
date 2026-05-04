package com.personal.accountantAssistant.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.extensions.toPeriodDateStr
import com.personal.accountantAssistant.ui.theme.Dimens
import java.util.Date

@Composable
fun HomeHeader(
    periodDates: Pair<Date?, Date?>?,
    availableText: String,
    availableColor: Color,
    walletIconColor: Color,
    onDatePickerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.spacingMd),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_wallet),
                contentDescription = null,
                tint = walletIconColor,
                modifier = Modifier.size(Dimens.iconSize)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimens.spacingSm)
            ) {
                Text(
                    text = stringResource(R.string.period_to_expense).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = Dimens.textMd,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = periodDates?.toPeriodDateStr().orEmpty(),
                    fontSize = Dimens.textMd,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = availableText.uppercase(),
                    color = availableColor,
                    fontSize = Dimens.textMd,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onDatePickerClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_today),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.iconSize)
                )
            }
        }
    }
}