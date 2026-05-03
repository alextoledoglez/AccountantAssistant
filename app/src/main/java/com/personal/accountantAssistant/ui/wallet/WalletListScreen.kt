package com.personal.accountantAssistant.ui.wallet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.enums.FlipperViews
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr
import com.personal.accountantAssistant.ui.theme.Dimens
import com.personal.accountantAssistant.ui.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletListScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    flipper: FlipperViews?,
    items: List<CardModel>,
    onRefresh: () -> Unit,
    onEdit: (CardModel) -> Unit,
    onActive: (CardModel) -> Unit,
    onDelete: (CardModel) -> Unit
) {
    when (flipper) {
        FlipperViews.LOADER -> Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }

        else -> PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = onRefresh,
            modifier = modifier
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = Dimens.spacingMd,
                    vertical = Dimens.spacingXs
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items, key = { it.id }) { card ->
                    CardListItem(
                        model = card,
                        onEdit = { onEdit(card) },
                        onActive = { onActive(card.copy(isActive = !card.isActive)) },
                        onDelete = { onDelete(card) }
                    )
                    Spacer(modifier = Modifier.height(Dimens.spacingXs))
                }
            }
        }
    }
}

@Composable
fun CardListItem(
    model: CardModel,
    onEdit: () -> Unit,
    onActive: () -> Unit,
    onDelete: () -> Unit
) {
    val isActive = model.isActive

    val textColor = if (isActive)
        MaterialTheme.colorScheme.onSurface
    else
        MaterialTheme.extendedColors.onSurfaceDisabled

    val chipIconTint = if (isActive)
        MaterialTheme.extendedColors.inherit
    else
        MaterialTheme.extendedColors.onSurfaceDisabled

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingMd)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = model.company,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = Dimens.textMd,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = model.name,
                    color = textColor,
                    fontSize = Dimens.textSm,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chip),
                    contentDescription = null,
                    tint = chipIconTint,
                    modifier = Modifier.size(Dimens.iconSize)
                )
                Text(
                    text = model.availableValue.toCurrencyMaskedStr(),
                    color = textColor,
                    fontSize = Dimens.textXl,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = Dimens.iconSize),
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = isActive,
                    onCheckedChange = { onActive() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.extendedColors.switchCheckedThumbColor,
                        checkedTrackColor = MaterialTheme.extendedColors.switchCheckedTrackColor
                    )
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete_red),
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.extendedColors.inherit
                    )
                }
            }
        }
    }
}