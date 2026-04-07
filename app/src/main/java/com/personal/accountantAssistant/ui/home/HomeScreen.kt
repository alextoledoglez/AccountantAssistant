package com.personal.accountantAssistant.ui.home

import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.personal.accountantAssistant.BuildConfig
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.enums.FlipperViews
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.providers.AdProvider
import java.math.BigDecimal
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    adProvider: AdProvider?,
    onShowDatePicker: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    val isLoading by viewModel.isLoading.observeAsState(false)
    val flipper by viewModel.flipper.observeAsState()
    val periodDates by viewModel.periodDates.observeAsState()
    val availableMoney by viewModel.availableMoney.observeAsState()
    val expensesValues by viewModel.expensesValues.observeAsState()

    LaunchedEffect(periodDates, availableMoney) {
        periodDates?.let { period -> viewModel.loadExpenses(period, availableMoney) }
    }

    val context = LocalContext.current
    val primaryColorInt = ContextCompat.getColor(context, R.color.primaryColor)
    val successColorInt = ContextCompat.getColor(context, R.color.successColor)
    val errorColorInt = ContextCompat.getColor(context, R.color.errorColor)

    val buysText = stringResource(R.string.menu_buys)
    val billsText = stringResource(R.string.menu_bills)
    val totalText = stringResource(R.string.total)
    val gainText = stringResource(R.string.gain)
    val missingText = stringResource(R.string.missing)
    val availableText = stringResource(R.string.available_value,
        expensesValues?.available.orZero().abs().toCurrencyMaskedStr())

    fun expenseColor(expense: BigDecimal) =
        if (expense.isMoreThan(availableMoney)) errorColorInt else successColorInt
    fun conditionColor(cond: Boolean) = if (cond) successColorInt else errorColorInt

    val buysVal = expensesValues?.buys.orZero()
    val billsVal = expensesValues?.bills.orZero()
    val totalVal = expensesValues?.total.orZero().rounded()
    val balanceVal = expensesValues?.balance.orZero()
    val isTotalLessThanAvailable = expensesValues?.isTotalLessThanAvailable ?: true
    val availableColor = conditionColor(isTotalLessThanAvailable)

    val dashboardItems = remember(expensesValues, availableMoney) {
        listOf(
            DashboardItemModel(R.drawable.ic_buys, buysText, expenseColor(buysVal), buysVal),
            DashboardItemModel(R.drawable.ic_bills, billsText, expenseColor(billsVal), billsVal),
            DashboardItemModel(
                R.drawable.ic_money,
                if (balanceVal.isMoreThanOrEqualToZero()) gainText else missingText,
                conditionColor(balanceVal.isMoreThanOrEqualToZero()),
                balanceVal
            ),
            DashboardItemModel(R.drawable.ic_total, totalText, expenseColor(totalVal), totalVal)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.backgroundColor))
    ) {
        HomeHeader(
            periodDates = periodDates,
            availableText = availableText,
            availableColor = Color(availableColor),
            walletIconColor = Color(availableColor),
            onDatePickerClick = onShowDatePicker
        )

        when (flipper) {
            FlipperViews.LOADER -> Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = colorResource(R.color.primaryColor)) }

            else -> {
                androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                    isRefreshing = isLoading,
                    onRefresh = {
                        viewModel.loadPeriodDates()
                        viewModel.loadAvailableMoney()
                    },
                    modifier = Modifier.weight(0.7f)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(
                            horizontal = dimensionResource(R.dimen.default_material_margin),
                            vertical = dimensionResource(R.dimen.default_material_margin)
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(dashboardItems) { item ->
                            HomeGridItem(item = item, onClick = { onItemClick(item.drawableRes) })
                        }
                    }
                }

                if (adProvider != null) {
                    AndroidView(
                        factory = { ctx ->
                            FrameLayout(ctx).also { container ->
                                adProvider.loadAdOn(container)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.3f)
                            .padding(horizontal = dimensionResource(R.dimen.default_material_margin))
                    )
                }

                Text(
                    text = stringResource(R.string.app_version, BuildConfig.VERSION_NAME),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(R.dimen.default_material_margin),
                            vertical = dimensionResource(R.dimen.small_material_margin)
                        ),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun HomeHeader(
    periodDates: Pair<Date?, Date?>?,
    availableText: String,
    availableColor: Color,
    walletIconColor: Color,
    onDatePickerClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.default_material_margin)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.half_material_margin)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_wallet),
                contentDescription = null,
                tint = walletIconColor,
                modifier = Modifier.size(48.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimensionResource(R.dimen.half_material_margin))
            ) {
                Text(
                    text = stringResource(R.string.period_to_expense).uppercase(),
                    color = colorResource(R.color.primaryColor),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = periodDates?.toPeriodDateStr().orEmpty(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = availableText.uppercase(),
                    color = availableColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onDatePickerClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_today),
                    contentDescription = null,
                    tint = colorResource(R.color.primaryColor),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
fun HomeGridItem(item: DashboardItemModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.small_material_margin))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.card_view_content_padding)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(item.drawableRes),
                contentDescription = null,
                tint = Color(item.color),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(top = dimensionResource(R.dimen.small_material_margin))
            )
            Text(
                text = item.text,
                color = Color(item.color),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.small_material_margin))
            )
            Text(
                text = item.value.abs().toCurrencyMaskedStr(),
                color = Color(item.color),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(R.dimen.small_material_margin))
            )
        }
    }
}