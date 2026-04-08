package com.personal.accountantAssistant.ui.wallet

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.ui.common.ListSummaryCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun WalletScreen() {
    val fragmentManager = (LocalActivity.current as? FragmentActivity)?.supportFragmentManager
    val viewModel: WalletViewModel = koinViewModel()

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

    LaunchedEffect(Unit) { viewModel.loadCards() }

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
        WalletListScreen(
            modifier = Modifier.weight(1f),
            isLoading = isLoading,
            flipper = flipper,
            items = filteredCards,
            onRefresh = { viewModel.loadCards() },
            onEdit = { card ->
                fragmentManager?.let {
                    WalletDetailsFragment.showDialogFragment(
                        model = card,
                        onEdit = viewModel::saveCard,
                        manager = it
                    )
                }
            },
            onActive = viewModel::switchActiveCard,
            onDelete = { pendingDelete = it }
        )
    }

    pendingDelete?.let { model ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.delete_record_title)) },
            text = { Text(stringResource(R.string.delete_record_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCard(model)
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