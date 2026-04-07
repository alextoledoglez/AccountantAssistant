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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.enums.FlipperViews
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.ui.expenses.ListSummaryCard

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
            .background(colorResource(R.color.backgroundColor))
    ) {
        ListSummaryCard(
            summary = summary,
            itemCount = filteredCards.size,
            searchQuery = searchQuery,
            onSearch = { searchQuery = it },
            onToggleAll = { viewModel.setAllCardsActive(it) }
        )

        when (flipper) {
            FlipperViews.LOADER -> Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = colorResource(R.color.primaryColor)) }

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
                        Spacer(modifier = Modifier.height(4.dp))
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
    val textColor = if (isActive) colorResource(R.color.fontColor) else colorResource(R.color.disableFontColor)
    val chipColor = if (isActive) colorResource(R.color.chipColor) else colorResource(R.color.disableChipColor)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chip),
                    contentDescription = null,
                    tint = chipColor,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = model.availableValue.toCurrencyMaskedStr(),
                    color = textColor,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 48.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                        checkedThumbColor = colorResource(R.color.primaryColor),
                        checkedTrackColor = colorResource(R.color.primaryColor).copy(alpha = 0.5f)
                    )
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete_red),
                        contentDescription = stringResource(R.string.delete),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}