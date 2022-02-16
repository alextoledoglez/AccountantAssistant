package com.personal.accountantAssistant.ui.home

import android.os.Build
import android.util.TypedValue
import android.view.View
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
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.viewBinding
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.MenuHelper
import com.personal.accountantAssistant.utils.NumberUtils
import kotlin.math.abs

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
                binding.availableSection.tvPeriodValue.text = it ?: Constants.DASH_SEPARATOR
            }
            availableMoney.observe(viewLifecycleOwner, ::settingAvailableMoneyCard)
            dashboardValues.observe(viewLifecycleOwner, adapter::submitList)
            expensesValues.observe(viewLifecycleOwner, ::settingDashboardItems)
        }
        with(binding) {
            rvDashboard.adapter = adapter
            viewModel.calculateExpenses()
            availableSection.ibDateRangePicker.setOnClickListener { showRangePicker() }
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

    private fun settingAvailableMoneyCard(value: Double?) {
        val isExpensesLessThanAvailable = viewModel.isExpensesLessThanAvailable(
            viewModel.totalExpenses.value
        )
        val availableColor = getColorResourceBy(isExpensesLessThanAvailable)
        binding.availableSection.availableCard.apply {
            ivCardImage.apply {
                setImageResource(R.drawable.ic_wallet)
                setColorFilter(
                    context.getColor(availableColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                visibility = View.VISIBLE
            }
            tvCardTitle.apply {
                text = context.getString(titleRes.available)
                setTextColor(context.getColor(availableColor))
                setBackgroundColor(context.getColor(R.color.colorWhite))
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.orZero())
            }

            tvCardSubtitle.apply {
                text = abs(value.orZero()).toString()
                setTextColor(context.getColor(availableColor))
            }
        }
    }

    private fun settingDashboardItems(values: ExpensesValuesModel?) {

        val availableMoney = viewModel.availableMoney.value.orZero()
        val buys = values?.buys.orZero()
        val bills = values?.bills.orZero()
        val total = NumberUtils.roundTo(values?.total.orZero())
        val gainOrNeededValue = NumberUtils.roundTo(availableMoney.minus(total))
        val isZeroLessThanGainOrNeeded = viewModel.isZeroLessThan(gainOrNeededValue)
        val gainOrNeededTitle = if (isZeroLessThanGainOrNeeded) titleRes.gain else titleRes.missing
        val gainOrNeededColor = getColorResourceBy(isZeroLessThanGainOrNeeded)

        viewModel.postDashboardValues(
            listOf(
                DashboardItemModel(
                    R.drawable.ic_buys, titleRes.buys, getExpenseColorResourceBy(buys), buys
                ),
                DashboardItemModel(
                    R.drawable.ic_bills, titleRes.bills, getExpenseColorResourceBy(bills), bills
                ),
                DashboardItemModel(
                    R.drawable.ic_money, gainOrNeededTitle, gainOrNeededColor, gainOrNeededValue
                ),
                DashboardItemModel(
                    R.drawable.ic_total, titleRes.total, getExpenseColorResourceBy(total), total
                )
            )
        )
    }

    private fun getColorResourceBy(
        isCondition: Boolean?,
        @ColorRes trueResource: Int = colorRes.success,
        @ColorRes falseResource: Int = colorRes.error
    ) = if (isCondition == true) trueResource else falseResource

    private fun getExpenseColorResourceBy(expense: Double?): Int = getColorResourceBy(
        viewModel.isExpensesMoreThanAvailable(expense),
        colorRes.error,
        colorRes.success
    )

    companion object {
        fun newInstance() = HomeFragment()
    }
}