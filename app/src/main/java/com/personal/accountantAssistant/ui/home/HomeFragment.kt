package com.personal.accountantAssistant.ui.home

import android.view.Menu
import android.view.MenuInflater
import com.google.android.material.datepicker.MaterialDatePicker
import com.personal.accountantAssistant.BuildConfig
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.databinding.FragmentHomeBinding
import com.personal.accountantAssistant.domain.models.ColorResourcesModel
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.domain.models.ExpensesValuesModel
import com.personal.accountantAssistant.domain.models.TitleResourcesModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.providers.AdProvider
import org.koin.android.ext.android.inject
import java.math.BigDecimal


class HomeFragment : BaseFragment<HomeViewModel>() {

    override val binding by viewBinding(FragmentHomeBinding::inflate)

    private val lytHeader by lazy { binding.lytHeader }
    private val ibDateRangePicker by lazy { lytHeader.ibDateRangePicker }
    private val tvPeriodValue by lazy { lytHeader.tvPeriodValue }
    private val lytContent by lazy { binding.lytContent }
    private val tvVersion by lazy { binding.tvVersion }
    private val srlContent by lazy { lytContent.srlContent }
    private val vfContent by lazy { lytContent.vfContent }
    private val rvContent by lazy { lytContent.rvContent }

    private var titleRes: TitleResourcesModel = TitleResourcesModel()
    private var colorRes: ColorResourcesModel = ColorResourcesModel()
    private val adapter by lazy { HomeListAdapter() }
    private val adProvider: AdProvider? by inject()

    override fun onDestroy() {
        super.onDestroy()
        adProvider?.destroyAd()
        rvContent.destroyAdapter()
    }

    override fun onPause() {
        super.onPause()
        adProvider?.pauseAd()
    }

    override fun onResume() {
        super.onResume()
        adProvider?.resumeAd()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.hideMenuOptions()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun initComponents() {
        ibDateRangePicker.setOnClickListener { showRangePicker() }
        srlContent.setOnRefreshListener { loadData() }
        rvContent.setGridLayoutAdapter(adapter, spanCount = 2)
        adProvider?.loadAdOn(binding.flAds)
        tvVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) { srlContent.updateRefreshing(it.orFalse()) }
            flipper.observe(viewLifecycleOwner) { vfContent.updateDisplayedChild(it.ordinal) }
            periodDates.observe(viewLifecycleOwner) {
                tvPeriodValue.text = it.toPeriodDateStr()
                loadExpenses(it.second, availableMoney.value)
            }
            availableMoney.observe(viewLifecycleOwner) {
                loadExpenses(periodDates.value?.second, it)
            }
            expensesValues.observe(viewLifecycleOwner, ::settingDashboardItems)
            dashboardValues.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                srlContent.stopRefreshing()
                rvContent.scrollToTop()
            }
        }
        loadData()
    }

    private fun showRangePicker() {
        MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.select_period))
            .setSelection(viewModel.getSelectedPeriod())
            .build()
            .apply {
                addOnPositiveButtonClickListener { period ->
                    viewModel.savePeriodDates(period)
                    loadData()
                }
            }
            .show(requireActivity().supportFragmentManager, String.EMPTY)
    }

    private fun settingDashboardItems(values: ExpensesValuesModel?) {

        //Values
        val buys = values?.buys.orZero()
        val bills = values?.bills.orZero()
        val total = values?.total.orZero().rounded()
        val available = values?.available.orZero()
        val balance = values?.balance.orZero()
        val availableColor = getColorResourceBy(values?.isTotalLessThanAvailable)

        //Available money
        binding.lytHeader.apply {
            ivAvailable.setColorFilter(availableColor, android.graphics.PorterDuff.Mode.SRC_IN)
            tvAvailable.apply {
                text = getString(R.string.available_value, available.abs().toCurrencyMaskedStr())
                setTextColor(availableColor)
            }
        }

        //Expenses texts
        val buysText = getString(titleRes.buys)
        val billsText = getString(titleRes.bills)
        val totalText = getString(titleRes.total)

        //Expenses colors
        val buysColor = getExpensesColorResourceBy(buys)
        val billsColor = getExpensesColorResourceBy(bills)
        val totalColor = getExpensesColorResourceBy(total)

        //Balance
        val isBalanceMoreThanOrEqualToZero = balance.isMoreThanOrEqualToZero()
        val balanceTextRes = if (isBalanceMoreThanOrEqualToZero) titleRes.gain else titleRes.missing
        val balanceText = getString(balanceTextRes)
        val balanceColor = getColorResourceBy(isBalanceMoreThanOrEqualToZero)

        viewModel.postDashboardValues(
            listOf(
                DashboardItemModel(R.drawable.ic_buys, buysText, buysColor, buys),
                DashboardItemModel(R.drawable.ic_bills, billsText, billsColor, bills),
                DashboardItemModel(R.drawable.ic_money, balanceText, balanceColor, balance),
                DashboardItemModel(R.drawable.ic_total, totalText, totalColor, total)
            )
        )
    }

    private fun getColorResourceBy(condition: Boolean?) = requireContext().getCompatColor(
        condition, colorRes.success, colorRes.error
    )

    private fun getExpensesColorResourceBy(expenses: BigDecimal) = requireContext().getCompatColor(
        expenses.isMoreThan(viewModel.availableMoney.value), colorRes.error, colorRes.success
    )

    private fun loadData() {
        viewModel.apply {
            loadPeriodDates()
            loadAvailableMoney()
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}