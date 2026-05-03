package com.personal.accountantAssistant.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr
import com.personal.accountantAssistant.ui.theme.Dimens
import com.personal.accountantAssistant.ui.theme.extendedColors

@Composable
fun ListSummaryCard(
    summary: SummaryModel,
    itemCount: Int,
    searchQuery: String,
    onSearch: (String) -> Unit,
    onToggleAll: (Boolean) -> Unit
) {
    val isAnyActive = summary.isAnyActive()
    val isAllActive = summary.isActiveCountEqualTo(itemCount)
    val accentColor =
        if (isAnyActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.spacingSm),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingSm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_money),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(Dimens.iconSize)
                )
                Text(
                    text = summary.total.toCurrencyMaskedStr(),
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = Dimens.textXxl,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isAllActive,
                    onCheckedChange = onToggleAll,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.extendedColors.switchCheckedThumbColor,
                        checkedTrackColor = MaterialTheme.extendedColors.switchCheckedTrackColor
                    )
                )
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearch,
                placeholder = { Text(stringResource(R.string.search_view_hint_message)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.spacingXs),
                singleLine = true
            )
        }
    }
}