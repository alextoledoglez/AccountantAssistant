package com.personal.accountantAssistant.ui.home

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import com.google.android.material.datepicker.MaterialDatePicker
import com.personal.accountantAssistant.core.BaseFragment
import com.personal.accountantAssistant.core.extensions.drawFrom
import com.personal.accountantAssistant.core.extensions.viewBinding
import com.personal.accountantAssistant.databinding.FragmentHomeBinding
import com.personal.accountantAssistant.utils.MenuHelper
import kotlinx.android.synthetic.main.layout_home_chart_card_view.*
import kotlinx.android.synthetic.main.layout_home_title_card.*

class HomeFragment : BaseFragment<HomeViewModel>() {

    override val binding by viewBinding(FragmentHomeBinding::inflate)

    @RequiresApi(Build.VERSION_CODES.P)
    override fun setupView() {
        MenuHelper.initializeHomeOptions()
        with(viewModel) {
            availableMoney.observe(viewLifecycleOwner, { updateAvailableMoney(it) })
            expensedMoney.observe(viewLifecycleOwner, { pcContent.drawFrom(it, periodText.value) })
            viewModel.setAvailableMoney()
        }
        with(binding) {
            calculateExpensesOn(root)
            etCardSubtitle?.apply {
                setText(viewModel.getAvailableMoneyStr())
                doOnTextChanged { text, _, _, _ ->
                    viewModel.updateAvailableMoney(text.toString())
                    calculateExpensesOn(root)
                }
            }
            mbDateRangePicker.setOnClickListener { showRangePicker() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showRangePicker() {
        MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select dates")
            .setSelection(viewModel.getSelectedPeriod())
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    viewModel.savePeriodDates(it?.first, it?.second)
                    calculateExpensesOn(binding.root)
                }
            }
            .show(requireActivity().supportFragmentManager, "Test")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun calculateExpensesOn(rootView: View) = viewModel.calculateExpenses(context, rootView)

    companion object {
        fun newInstance() = HomeFragment()
    }
}