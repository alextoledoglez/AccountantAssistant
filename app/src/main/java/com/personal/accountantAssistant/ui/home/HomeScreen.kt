package com.personal.accountantAssistant.ui.home

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.enums.FlipperViews
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.isMoreThan
import com.personal.accountantAssistant.extensions.isMoreThanOrEqualToZero
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.rounded
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr
import com.personal.accountantAssistant.providers.AdProvider
import com.personal.accountantAssistant.ui.common.AdaptiveScreen
import com.personal.accountantAssistant.ui.home.components.HomeHeader
import com.personal.accountantAssistant.ui.home.components.HomeLandscapeContent
import com.personal.accountantAssistant.ui.home.components.HomePortraitContent
import com.personal.accountantAssistant.ui.theme.Dimens
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigateTo: (tab: TabPositions) -> Unit) {
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val fragmentManager = (LocalActivity.current as? FragmentActivity)?.supportFragmentManager
    val viewModel: HomeViewModel = koinViewModel()
    val adProvider: AdProvider? = koinInject()

    val isLoading by viewModel.isLoading.observeAsState(false)
    val flipper by viewModel.flipper.observeAsState()
    val periodDates by viewModel.periodDates.observeAsState()
    val availableMoney by viewModel.availableMoney.observeAsState()
    val expensesValues by viewModel.expensesValues.observeAsState()

    val successColorInt = MaterialTheme.colorScheme.primary.toArgb()
    val errorColorInt = MaterialTheme.colorScheme.error.toArgb()

    val availableText = stringResource(
        R.string.available_value,
        expensesValues?.available.orZero().abs().toCurrencyMaskedStr()
    )

    fun expenseColor(expense: BigDecimal) = if (expense.isMoreThan(availableMoney))
        errorColorInt
    else
        successColorInt

    fun conditionColor(cond: Boolean) = if (cond) successColorInt else errorColorInt

    val buysVal = expensesValues?.buys.orZero()
    val billsVal = expensesValues?.bills.orZero()
    val totalVal = expensesValues?.total.orZero().rounded()
    val balanceVal = expensesValues?.balance.orZero()
    val isTotalLessThanAvailable = expensesValues?.isTotalLessThanAvailable ?: true
    val availableColor = conditionColor(isTotalLessThanAvailable)

    val dashboardItems = remember(expensesValues, availableMoney) {
        listOf(
            DashboardItemModel(
                R.drawable.ic_buys,
                R.string.menu_buys,
                expenseColor(buysVal),
                buysVal
            ),
            DashboardItemModel(
                R.drawable.ic_bills,
                R.string.menu_bills,
                expenseColor(billsVal),
                billsVal
            ),
            DashboardItemModel(
                R.drawable.ic_money,
                if (balanceVal.isMoreThanOrEqualToZero()) R.string.gain else R.string.missing,
                conditionColor(balanceVal.isMoreThanOrEqualToZero()),
                balanceVal
            ),
            DashboardItemModel(
                R.drawable.ic_total,
                R.string.total,
                expenseColor(totalVal),
                totalVal
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadPeriodDates()
        viewModel.loadAvailableMoney()
    }

    LaunchedEffect(periodDates, availableMoney) {
        val period = periodDates ?: return@LaunchedEffect
        viewModel.loadExpenses(period, availableMoney)
    }

    val onDatePickerClick: () -> Unit = {
        fragmentManager?.let {
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(R.string.select_period)
                .setSelection(viewModel.getSelectedPeriod())
                .build()
                .apply {
                    addOnPositiveButtonClickListener { period ->
                        viewModel.savePeriodDates(period)
                        viewModel.loadPeriodDates()
                        viewModel.loadAvailableMoney()
                    }
                }
                .show(it, String.EMPTY)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isPortrait) {
            HomeHeader(
                periodDates = periodDates,
                availableText = availableText,
                availableColor = Color(availableColor),
                walletIconColor = Color(availableColor),
                onDatePickerClick = onDatePickerClick
            )
        }

        when (flipper) {
            FlipperViews.LOADER -> Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }

            else -> {
                androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                    isRefreshing = isLoading,
                    onRefresh = {
                        viewModel.loadPeriodDates()
                        viewModel.loadAvailableMoney()
                    },
                    modifier = Modifier.weight(0.7f)
                ) {
                    AdaptiveScreen(
                        portrait = { HomePortraitContent(dashboardItems, navigateTo) },
                        landscape = {
                            HomeLandscapeContent(
                                periodDates = periodDates,
                                availableText = availableText,
                                availableColor = Color(availableColor),
                                walletIconColor = Color(availableColor),
                                dashboardItems = dashboardItems,
                                onDatePickerClick = onDatePickerClick,
                                navigateTo = navigateTo
                            )
                        }
                    )
                }

                if (adProvider != null && isPortrait) {
                    val frameLayout = adProvider.rememberFrameLayoutWithLifecycle()
                    AndroidView(
                        factory = { frameLayout },
                        update = { container -> adProvider.loadAdOn(container) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.3f)
                            .padding(horizontal = Dimens.spacingMd)
                    )
                }
            }
        }
    }
}