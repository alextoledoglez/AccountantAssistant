package com.personal.accountantAssistant.ui.expenses

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
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
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesListScreen(
    @StringRes titleRes: Int,
    isLoading: Boolean,
    flipper: FlipperViews?,
    items: List<ExpenseModel>,
    summary: SummaryModel,
    searchQuery: String,
    onSearch: (String) -> Unit,
    onToggleAll: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    onEdit: (ExpenseModel) -> Unit,
    onActive: (ExpenseModel) -> Unit,
    onDelete: (ExpenseModel) -> Unit,
    showDate: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.backgroundColor))
    ) {
        ListSummaryCard(
            title = stringResource(titleRes),
            summary = summary,
            itemCount = items.size,
            searchQuery = searchQuery,
            onSearch = onSearch,
            onToggleAll = onToggleAll
        )

        when (flipper) {
            FlipperViews.LOADER -> Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = colorResource(R.color.primaryColor)) }

            else -> PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = onRefresh,
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = dimensionResource(R.dimen.default_material_margin),
                        vertical = dimensionResource(R.dimen.small_material_margin)
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items, key = { it.id }) { expense ->
                        ExpenseListItem(
                            model = expense,
                            showDate = showDate,
                            onEdit = { onEdit(expense) },
                            onActive = { onActive(expense.copy(isActive = !expense.isActive)) },
                            onDelete = { onDelete(expense) }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ListSummaryCard(
    title: String,
    summary: SummaryModel,
    itemCount: Int,
    searchQuery: String,
    onSearch: (String) -> Unit,
    onToggleAll: (Boolean) -> Unit
) {
    val isAnyActive = summary.isAnyActive()
    val isAllActive = summary.isActiveCountEqualTo(itemCount)
    val accentColor = if (isAnyActive) colorResource(R.color.redColor) else colorResource(R.color.primaryColor)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.half_material_margin)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.card_view_content_padding))
        ) {
            Text(
                text = title.uppercase(),
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_money),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier
                        .size(48.dp)
                        .weight(1f)
                )
                Text(
                    text = summary.total.toCurrencyMaskedStr(),
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.weight(0.5f)
                )
                Switch(
                    checked = isAllActive,
                    onCheckedChange = onToggleAll,
                    modifier = Modifier.weight(1f),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorResource(R.color.primaryColor),
                        checkedTrackColor = colorResource(R.color.primaryColor).copy(alpha = 0.5f)
                    )
                )
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearch,
                placeholder = { Text(stringResource(R.string.search_view_hint_message)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.small_material_margin)),
                singleLine = true
            )
        }
    }
}

@Composable
fun ExpenseListItem(
    model: ExpenseModel,
    showDate: Boolean,
    onEdit: () -> Unit,
    onActive: () -> Unit,
    onDelete: () -> Unit
) {
    val textColor = if (model.isActive) colorResource(R.color.fontColor) else colorResource(R.color.disableFontColor)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.half_material_margin)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(0.7f)) {
                Text(
                    text = model.name.orEmpty(),
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.height(30.dp).wrapContentHeight()
                )
                if (showDate) {
                    Text(
                        text = model.date.toDateStr(),
                        color = textColor,
                        fontSize = 12.sp,
                        modifier = Modifier.height(30.dp).wrapContentHeight()
                    )
                }
                Text(
                    text = formatExpenseValue(model, showDate),
                    color = textColor,
                    fontSize = 12.sp,
                    modifier = Modifier.height(30.dp).wrapContentHeight()
                )
            }
            Row(
                modifier = Modifier.weight(0.3f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = model.isActive,
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

private fun formatExpenseValue(model: ExpenseModel, isBill: Boolean): String {
    val unitaryPriceStr = model.unitaryValue.toCurrencyMaskedStr()
    val totalPriceStr = model.calculateTotalValue().toCurrencyMaskedStr()
    return if (isBill) {
        val quantityStr = model.quantity.toString() + String.TIMES
        "$quantityStr${unitaryPriceStr}${String.EQUAL_OPERATOR}${totalPriceStr}"
    } else {
        val quantityStr = model.quantity.toString() + String.UNITY
        "$quantityStr${String.MULTIPLY_OPERATOR}${unitaryPriceStr}${String.EQUAL_OPERATOR}${totalPriceStr}"
    }
}