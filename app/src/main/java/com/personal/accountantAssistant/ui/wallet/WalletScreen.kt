package com.personal.accountantAssistant.ui.wallet

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.containStr
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.ui.common.ListSummaryCard
import com.personal.accountantAssistant.ui.common.PrimaryFabButton
import com.personal.accountantAssistant.ui.theme.Dimens
import com.personal.accountantAssistant.ui.wallet.details.WalletDetailsFragment
import org.koin.androidx.compose.koinViewModel

@Composable
fun WalletScreen(
    onSetFab: ((@Composable () -> Unit)?) -> Unit = {},
    onSetDeleteAll: ((() -> Unit)?) -> Unit = {}
) {
    val fragmentManager = (LocalActivity.current as? FragmentActivity)?.supportFragmentManager
    val viewModel: WalletViewModel = koinViewModel()

    val isLoading by viewModel.isLoading.observeAsState(false)
    val flipper by viewModel.flipper.observeAsState()
    val cards by viewModel.cards.observeAsState(emptyList())
    val summary by viewModel.summary.observeAsState(SummaryModel())

    var searchQuery by rememberSaveable { mutableStateOf("") }
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
    val deleteAllContent: () -> Unit = remember(viewModel) { viewModel::deleteAllCards }
    val fabContent: @Composable () -> Unit = remember(fragmentManager) {
        { WalletFabs(fragmentManager, viewModel) }
    }

    LaunchedEffect(Unit) { viewModel.loadCards() }

    SideEffect {
        onSetFab(fabContent)
        onSetDeleteAll(deleteAllContent)
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

@Composable
internal fun WalletFabs(fragmentManager: FragmentManager?, viewModel: WalletViewModel) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        PrimaryFabButton(
            isVisible = true,
            painterResourceId = R.drawable.ic_add_white,
            stringResourceId = R.string.add_action,
            onClick = {
                fragmentManager?.let {
                    WalletDetailsFragment.showDialogFragment(
                        model = CardModel(),
                        onEdit = viewModel::saveCard,
                        manager = it
                    )
                }
            }
        )
    }
}