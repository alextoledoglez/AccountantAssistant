package com.personal.accountantAssistant.ui.home

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BaseFragment
import com.personal.accountantAssistant.databinding.FragmentHomeBinding
import com.personal.accountantAssistant.databinding.LayoutHomeCardBinding
import com.personal.accountantAssistant.domain.models.home.SummaryModel
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.viewBinding
import com.personal.accountantAssistant.utils.MenuHelper

@RequiresApi(Build.VERSION_CODES.P)
class HomeFragment : BaseFragment<HomeViewModel>() {

    override val binding by viewBinding(FragmentHomeBinding::inflate)

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        calculateExpensesOn(binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun setupView() {
        MenuHelper.initializeHomeOptions()
        with(viewModel) {
            summaryValues.observe(viewLifecycleOwner, { summary ->
                setupHomeAvailableCardLayout(binding.availableSection.availableCard, summary)
                setupHomeNeededCardLayout(binding.summarySection.neededCard, summary)
                setupHomeTotalCardLayout(binding.summarySection.totalCard, summary)
            })
        }
        with(binding) {
            calculateExpensesOn(root)
            mbDateRangePicker.setOnClickListener { showRangePicker() }
        }
    }

    private fun setupHomeCardLayout(
        layout: LayoutHomeCardBinding, color: Int?, text: String?, value: Float?
    ) = layout.apply {
        val notNullColor = color ?: R.color.colorBlack
        ivCardImage.visibility = View.GONE

        tvCardTitle.text = text
        tvCardTitle.setTextColor(notNullColor)
        tvCardTitle.visibility = View.VISIBLE

        tvCardSubtitle.text = value?.orZero().toString()
        tvCardSubtitle.setTextColor(notNullColor)
        tvCardSubtitle.visibility = View.VISIBLE
    }

    private fun setupHomeNeededCardLayout(
        layout: LayoutHomeCardBinding, summaryModel: SummaryModel
    ) = setupHomeCardLayout(
        layout, summaryModel.neededColor, summaryModel.neededStr, summaryModel.needed
    )

    private fun setupHomeTotalCardLayout(
        layout: LayoutHomeCardBinding, summaryModel: SummaryModel
    ) = setupHomeCardLayout(
        layout, summaryModel.totalColor, summaryModel.totalStr, summaryModel.total
    )

    private fun setupHomeAvailableCardLayout(
        layout: LayoutHomeCardBinding, summaryModel: SummaryModel
    ) = setupHomeCardLayout(
        layout, summaryModel.availableColor, summaryModel.availableStr, summaryModel.available
    )

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showRangePicker() {
        MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.select_period))
            .setSelection(viewModel.getSelectedPeriod())
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    viewModel.savePeriodDates(it?.first, it?.second)
                    calculateExpensesOn(binding.root)
                }
            }
            .show(requireActivity().supportFragmentManager, String.EMPTY)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun calculateExpensesOn(rootView: View) = viewModel.calculateExpenses(context, rootView)

    companion object {
        fun newInstance() = HomeFragment()
    }
}