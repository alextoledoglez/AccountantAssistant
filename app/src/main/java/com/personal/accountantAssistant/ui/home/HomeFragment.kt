package com.personal.accountantAssistant.ui.home

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
import com.personal.accountantAssistant.utils.MenuHelper
import java.math.BigDecimal

class HomeFragment : BaseFragment<HomeViewModel>() {

    override val binding by viewBinding(FragmentHomeBinding::inflate)
    private var titleRes: TitleResourcesModel = TitleResourcesModel()
    private var colorRes: ColorResourcesModel = ColorResourcesModel()
    private val adapter by lazy { HomeListAdapter() }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvDashboard.adapter = null
    }

    override fun initComponents() {
        MenuHelper.initializeHomeOptions()
        with(binding) {
            srlLoader.setOnRefreshListener { viewModel.calculateExpenses() }
            lytHeader.ibDateRangePicker.setOnClickListener { showRangePicker() }
            rvDashboard.adapter = adapter
        }
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) { binding.srlLoader.isRefreshing = it.orFalse() }
            flipper.observe(viewLifecycleOwner) { binding.vfHome.displayedChild = it.ordinal }
            periodValue.observe(viewLifecycleOwner) {
                binding.lytHeader.tvPeriodValue.text = it ?: String.DASH_SEPARATOR
            }
            expensesValues.observe(viewLifecycleOwner, ::settingDashboardItems)
            dashboardValues.observe(viewLifecycleOwner) {
                adapter.submitList(it) { binding.srlLoader.stopRefreshing() }
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
        val result = available.minus(total).rounded()

        //Expenses colors
        val buysColor = getExpensesColorResourceBy(buys)
        val billsColor = getExpensesColorResourceBy(bills)
        val totalColor = getExpensesColorResourceBy(total)

        //Available color
        val isTotalLessThanAvailable = viewModel.isExpensesLessThanAvailable(total)
        val availableColor = getColorResourceBy(isTotalLessThanAvailable)

        //Results
        val isZeroLessThanResult = viewModel.isZeroLessThan(result)
        val resultTitle = if (isZeroLessThanResult) titleRes.gain else titleRes.missing
        val resultColor = getColorResourceBy(isZeroLessThanResult)

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
                DashboardItemModel(R.drawable.ic_money, resultTitle, resultColor, result),
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