package com.personal.accountantAssistant.ui.buys

import com.personal.accountantAssistant.adapters.BuysListAdapter
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.databinding.FragmentBuysBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr
import com.personal.accountantAssistant.extensions.viewBinding
import com.personal.accountantAssistant.ui.expenses.ExpensesFragment
import com.personal.accountantAssistant.utils.ImportExportUtils
import com.personal.accountantAssistant.utils.MenuHelper

class BuysFragment : ExpensesFragment<BuysViewModel>() {

    override val binding by viewBinding(FragmentBuysBinding::inflate)
    override val adapter by lazy {
        BuysListAdapter(
            ::onUpdateExpense, ::onDeleteExpense, ::onExpenseClicked
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvBuys.adapter = null
    }

    override fun initComponents() {
        super.initComponents()
        MenuHelper.initializeBuysOptions()
        initHeader(binding.headerCardTitlesBar)
        binding.rvBuys.adapter = adapter
    }

    override fun initObservers() {
        with(viewModel) {
            buys.observe(viewLifecycleOwner) {
                updateHeader(
                    binding.headerCardTitlesBar,
                    it.isAnyChecked,
                    it.isAllChecked,
                    it.total.toCurrencyMaskedStr()
                )
                adapter.submitList(it.expenses)
            }
            getBuys()
        }
    }

    override fun addMenuItemClickListener() {
        onExpenseClicked(ExpenseModel())
    }

    override fun importMenuItemClickListener() {
        ImportExportUtils.xlsImport(context, ExpensesType.BUY)
    }

    override fun exportMenuItemClickListener() {
        // ImportExportUtils.xlsExport(requireContext(), appDatabase, ExpensesType.BUY)
    }

    override fun deleteAllRecords() {
        viewModel.deleteAllBuys()
    }

    override fun restoreDefaultRecords() {
        viewModel.setDefaultBuys()
    }

    override fun onUpdateExpense(model: ExpenseModel) {
        viewModel.updateExpense(model)
    }

    override fun onDeleteExpense(model: ExpenseModel) {
        viewModel.deleteExpense(model)
    }

    companion object {
        fun newInstance() = BuysFragment()
    }

}