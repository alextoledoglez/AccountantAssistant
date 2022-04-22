package com.personal.accountantAssistant.ui.home

import android.view.Menu
import android.view.MenuInflater
import com.google.android.material.datepicker.MaterialDatePicker
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.adapters.HomeListAdapter
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.databinding.FragmentHomeBinding
import com.personal.accountantAssistant.domain.models.ColorResourcesModel
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.domain.models.ExpensesValuesModel
import com.personal.accountantAssistant.domain.models.TitleResourcesModel
import com.personal.accountantAssistant.extensions.*
import java.math.BigDecimal

class HomeFragment : BaseFragment<HomeViewModel>() {

    override val binding by viewBinding(FragmentHomeBinding::inflate)
    private val lytHeader by lazy { binding.lytHeader }
    private val lytContent by lazy { binding.lytContent }
    private var titleRes: TitleResourcesModel = TitleResourcesModel()
    private var colorRes: ColorResourcesModel = ColorResourcesModel()
    private val adapter by lazy { HomeListAdapter() }

    override fun onDestroy() {
        super.onDestroy()
        lytContent.rvContent.destroyAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.hideMenuOptions()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun initComponents() {
        with(lytHeader) { ibDateRangePicker.setOnClickListener { showRangePicker() } }
        with(lytContent) {
            srlContent.setOnRefreshListener { viewModel.calculateExpenses() }
            rvContent.setGridLayoutAdapter(adapter, spanCount = 2)
        }
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) {
                lytContent.srlContent.updateRefreshing(it.orFalse())
            }
            flipper.observe(viewLifecycleOwner) {
                lytContent.vfContent.updateDisplayedChild(it.ordinal)
            }
            periodValue.observe(viewLifecycleOwner) {
                lytHeader.tvPeriodValue.text = it ?: String.DASH_SEPARATOR
            }
            expensesValues.observe(viewLifecycleOwner, ::settingDashboardItems)
            dashboardValues.observe(viewLifecycleOwner) {
                adapter.submitList(it) { lytContent.srlContent.stopRefreshing() }
            }
            viewModel.calculateExpenses()
        }
    }

    private fun showRangePicker() {
        MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.select_period))
            .setSelection(viewModel.getSelectedPeriod())
            .build()
            .apply {
                addOnPositiveButtonClickListener { period ->
                    viewModel.savePeriodDates(period)
                    viewModel.calculateExpenses()
                }
            }
            .show(requireActivity().supportFragmentManager, String.EMPTY)
    }

    private fun settingDashboardItems(values: ExpensesValuesModel?) {

        //Values
        val buys = values?.buys.orZero()
        val bills = values?.bills.orZero()
        val available = viewModel.availableMoney.value.orZero()
        val total = values?.total.orZero().rounded()
        val balance = available.minus(total).rounded()

        //Expenses colors
        val buysColor = getExpensesColorResourceBy(buys)
        val billsColor = getExpensesColorResourceBy(bills)
        val totalColor = getExpensesColorResourceBy(total)

        //Available color
        val isTotalLessThanAvailable = viewModel.isExpensesLessThanAvailable(total)
        val availableColor = getColorResourceBy(isTotalLessThanAvailable)

        //Balance
        val isZeroLessThanBalance = viewModel.isZeroLessThan(balance)
        val balanceTitle = if (isZeroLessThanBalance) titleRes.gain else titleRes.missing
        val balanceColor = getColorResourceBy(isZeroLessThanBalance)

        binding.lytHeader.apply {
            ivAvailable.setColorFilter(availableColor, android.graphics.PorterDuff.Mode.SRC_IN)
            tvAvailable.apply {
                text = getString(R.string.available_value, available.abs().toCurrencyMaskedStr())
                setTextColor(availableColor)
            }
        }

        viewModel.postDashboardValues(
            listOf(
                DashboardItemModel(R.drawable.ic_buys, titleRes.buys, buysColor, buys),
                DashboardItemModel(R.drawable.ic_bills, titleRes.bills, billsColor, bills),
                DashboardItemModel(R.drawable.ic_money, balanceTitle, balanceColor, balance),
                DashboardItemModel(R.drawable.ic_total, titleRes.total, totalColor, total)
            )
        )
    }

    private fun getColorResourceBy(condition: Boolean?) = requireContext().getCompatColor(
        condition, colorRes.success, colorRes.error
    )

    private fun getExpensesColorResourceBy(expenses: BigDecimal) = requireContext().getCompatColor(
        viewModel.isExpensesMoreThanAvailable(expenses), colorRes.error, colorRes.success
    )

    companion object {
        fun newInstance() = HomeFragment()
    }
}