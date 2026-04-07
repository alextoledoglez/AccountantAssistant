package com.personal.accountantAssistant.ui.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.enums.FlipperViews
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.ui.common.ListSummaryCard
import com.personal.accountantAssistant.ui.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: WalletViewModel,
    onEdit: (CardModel) -> Unit,
    onActive: (CardModel) -> Unit,
    onDelete: (CardModel) -> Unit
) {
    val isLoading by viewModel.isLoading.observeAsState(false)
    val flipper by viewModel.flipper.observeAsState()
    val cards by viewModel.cards.observeAsState(emptyList())
    val summary by viewModel.summary.observeAsState(SummaryModel())

    var searchQuery by remember { mutableStateOf("") }
    var pendingDelete by remember { mutableStateOf<CardModel?>(null) }

    val filteredCards = remember(cards, searchQuery) {
        if (searchQuery.isBlank())
            cards.orEmpty()
        else cards?.filter {
            it.limitValue.toString().containStr(searchQuery) ||
                    it.availableValue.toString().containStr(searchQuery) ||
                    it.usedValue.toString().containStr(searchQuery) ||
                    it.password.containStr(searchQuery) ||
                    it.company.containStr(searchQuery) ||
                    it.name.containStr(searchQuery)
        }.orEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ListSummaryCard(
            summary = summary,
            itemCount = filteredCards.size.orZero(),
            searchQuery = searchQuery,
            onSearch = { searchQuery = it },
            onToggleAll = { viewModel.setAllCardsActive(it) }
        )

        when (flipper) {
            FlipperViews.LOADER -> Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }

            else -> PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = { viewModel.loadCards() },
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = dimensionResource(R.dimen.default_material_margin),
                        vertical = dimensionResource(R.dimen.small_material_margin)
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredCards, key = { it.id }) { card ->
                        CardListItem(
                            model = card,
                            onEdit = { onEdit(card) },
                            onActive = { onActive(card.copy(isActive = !card.isActive)) },
                            onDelete = { pendingDelete = card }
                        )
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.small_material_margin)))
                    }
                }
            }
        }
    }

    pendingDelete?.let { model ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.delete_record_title)) },
            text = { Text(stringResource(R.string.delete_record_message)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(model)
                    pendingDelete = null
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
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
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.card_view_elevation)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.default_material_margin))
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = model.company,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = model.name,
                    color = textColor,
                    fontSize = 12.sp,
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
                    modifier = Modifier.size(dimensionResource(R.dimen.image_button_size))
                )
                Text(
                    text = model.availableValue.toCurrencyMaskedStr(),
                    color = textColor,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = dimensionResource(R.dimen.image_button_size)),
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