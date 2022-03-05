package com.personal.accountantAssistant.ui.home

import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.adapters.home.HomeListAdapter
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.databinding.FragmentHomeBinding
import com.personal.accountantAssistant.domain.models.home.ColorResourcesModel
import com.personal.accountantAssistant.domain.models.home.DashboardItemModel
import com.personal.accountantAssistant.domain.models.home.ExpensesValuesModel
import com.personal.accountantAssistant.domain.models.home.TitleResourcesModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.utils.MenuHelper
import java.math.BigDecimal

@RequiresApi(Build.VERSION_CODES.P)
class HomeFragment : BaseFragment<HomeViewModel>() {

    override val binding by viewBinding(FragmentHomeBinding::inflate)
    private var titleRes: TitleResourcesModel = TitleResourcesModel()
    private var colorRes: ColorResourcesModel = ColorResourcesModel()
    private val adapter by lazy { HomeListAdapter() }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvDashboard.adapter = null
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        viewModel.calculateExpenses()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun setupView() {
        MenuHelper.initializeHomeOptions()
        with(viewModel) {
            periodValue.observe(viewLifecycleOwner) {
                binding.lytHeader.tvPeriodValue.text = it ?: String.DASH_SEPARATOR
            }
            expensesValues.observe(viewLifecycleOwner, ::settingDashboardItems)
            dashboardValues.observe(viewLifecycleOwner, adapter::submitList)
        }
        with(binding) {
            rvDashboard.adapter = adapter
            viewModel.calculateExpenses()
            lytHeader.ibDateRangePicker.setOnClickListener { showRangePicker() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
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

        //Colors
        val buysColor = getExpenseColorResourceBy(buys)
        val billsColor = getExpenseColorResourceBy(bills)
        val totalColor = getExpenseColorResourceBy(total)
        val availableColor = requireContext().getColor(
            getColorResourceBy(viewModel.isExpensesLessThanAvailable(total))
        )

        //Result
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

    private fun getColorResourceBy(
        isCondition: Boolean?,
        @ColorRes trueResource: Int = colorRes.success,
        @ColorRes falseResource: Int = colorRes.error
    ) = if (isCondition == true) trueResource else falseResource

    private fun getExpenseColorResourceBy(expense: BigDecimal?): Int = getColorResourceBy(
        viewModel.isExpensesMoreThanAvailable(expense),
        colorRes.error,
        colorRes.success
    )

    companion object {
        fun newInstance() = HomeFragment()
    }
}