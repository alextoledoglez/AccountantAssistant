package com.personal.accountantAssistant.ui.bills

import com.personal.accountantAssistant.adapters.BillsListAdapter
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.databinding.FragmentBillsBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr
import com.personal.accountantAssistant.extensions.viewBinding
import com.personal.accountantAssistant.ui.expenses.ExpensesFragment
import com.personal.accountantAssistant.utils.ImportExportUtils
import com.personal.accountantAssistant.utils.MenuHelper

class BillsFragment : ExpensesFragment<BillsViewModel>() {

    override val binding by viewBinding(FragmentBillsBinding::inflate)
    override val adapter by lazy {
        BillsListAdapter(
            ::onUpdateExpense, ::onDeleteExpense, ::onExpenseClicked
        )
    }

    override fun initComponents() {
        super.initComponents()
        MenuHelper.initializeBillsOptions()
        binding.srlLoader.setOnRefreshListener { viewModel.getBills() }
        binding.headerCardTitlesBar.apply {
            initHeader(binding.headerCardTitlesBar)
            titleSwitch.setOnClickListener { viewModel.setAllBillsActive(titleSwitch.isChecked) }
        }
        binding.rvBills.adapter = adapter
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) { binding.srlLoader.isRefreshing = it.orFalse() }
            flipper.observe(viewLifecycleOwner) { binding.vfBills.displayedChild = it.ordinal }
            isAllChecked.observe(viewLifecycleOwner) {
                binding.headerCardTitlesBar.titleSwitch.isChecked = it.orFalse()
                getBills()
            }
            isUpdated.observe(viewLifecycleOwner) { getBills() }
            isDeleted.observe(viewLifecycleOwner) { getBills() }
            bills.observe(viewLifecycleOwner) {
                updateHeader(
                    binding.headerCardTitlesBar,
                    it.isAnyChecked,
                    it.isAllChecked,
                    it.total.toCurrencyMaskedStr()
                )
                adapter.submitList(it.expenses)
            }
            getBills()
        }
    }

    override fun addMenuItemClickListener() {
        onExpenseClicked(ExpenseModel())
    }

    override fun importMenuItemClickListener() {
        ImportExportUtils.xlsImport(context, ExpensesType.BILL)
    }

    override fun exportMenuItemClickListener() {
        //ImportExportUtils.xlsExport(requireContext(), appDatabase, ExpensesType.BILL)
    }

    override fun deleteAllRecords() {
        viewModel.deleteAllBills()
    }

    override fun restoreDefaultRecords() {
        viewModel.setDefaultBills()
    }

    override fun onUpdateExpense(model: ExpenseModel) {
        viewModel.updateExpense(model)
    }

    override fun onDeleteExpense(model: ExpenseModel) {
        viewModel.deleteExpense(model)
    }

    companion object {
        fun newInstance() = BillsFragment()
    }

}