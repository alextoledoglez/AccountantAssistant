package com.personal.accountantAssistant.ui.expenses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.enums.FlipperViews
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.ui.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesListScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    flipper: FlipperViews?,
    items: List<ExpenseModel>,
    onRefresh: () -> Unit,
    onEdit: (ExpenseModel) -> Unit,
    onActive: (ExpenseModel) -> Unit,
    onDelete: (ExpenseModel) -> Unit,
    showDate: Boolean
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
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.small_material_margin)))
                }
            }
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
    val textColor = if (model.isActive)
        MaterialTheme.colorScheme.onSurface
    else
        MaterialTheme.extendedColors.onSurfaceDisabled

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.card_view_elevation))
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
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.text_field_height))
                        .wrapContentHeight()
                )
                if (showDate) {
                    Text(
                        text = model.date.toDateStr(),
                        color = textColor,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .height(dimensionResource(R.dimen.text_field_height))
                            .wrapContentHeight()
                    )
                }
                Text(
                    text = model.toFormatExpenseValue(showDate),
                    color = textColor,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.text_field_height))
                        .wrapContentHeight()
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