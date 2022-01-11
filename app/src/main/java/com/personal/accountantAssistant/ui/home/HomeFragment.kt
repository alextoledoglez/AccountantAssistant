package com.personal.accountantAssistant.ui.home

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayoutMediator
import com.personal.accountantAssistant.core.BaseFragment
import com.personal.accountantAssistant.core.extensions.EMPTY
import com.personal.accountantAssistant.core.extensions.viewBinding
import com.personal.accountantAssistant.databinding.FragmentHomeBinding
import com.personal.accountantAssistant.utils.MenuHelper

@RequiresApi(Build.VERSION_CODES.P)
class HomeFragment : BaseFragment<HomeViewModel>() {

    override val binding by viewBinding(FragmentHomeBinding::inflate)

    private lateinit var tlMediator: TabLayoutMediator

    override fun onDestroy() {
        super.onDestroy()
        tlMediator.detach()
    }

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
            .show(requireActivity().supportFragmentManager, String.EMPTY)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun calculateExpensesOn(rootView: View) = viewModel.calculateExpenses(context, rootView)

    companion object {
        fun newInstance() = HomeFragment()
    }
}