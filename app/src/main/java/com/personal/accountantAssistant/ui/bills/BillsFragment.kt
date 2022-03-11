package com.personal.accountantAssistant.ui.bills

import com.personal.accountantAssistant.adapters.BillsListAdapter
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.databinding.FragmentBillsBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.notify
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr
import com.personal.accountantAssistant.extensions.viewBinding
import com.personal.accountantAssistant.ui.expenses.ExpensesFragment
import com.personal.accountantAssistant.utils.ImportExportUtils
import com.personal.accountantAssistant.utils.MenuHelper

class BillsFragment : ExpensesFragment<BillsViewModel>() {

    override val binding by viewBinding(FragmentBillsBinding::inflate)
    override val adapter by lazy {
        BillsListAdapter(::onExpenseClicked, ::notifyItemChanged, ::notifyItemRemoved)
    }

    override fun initComponents() {
        super.initComponents()
        MenuHelper.initializeBillsOptions()
        binding.srlLoader.setOnRefreshListener { viewModel.getBills() }
        binding.lytHeader.apply {
            initHeader(this)
            scActive.setOnClickListener { notifyActiveItems(scActive.isChecked) }
        }
        binding.rvBills.adapter = adapter
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) {
                binding.srlLoader.isRefreshing = it.orFalse()
            }
            flipper.observe(viewLifecycleOwner) { binding.vfBills.displayedChild = it.ordinal }
            notify.observe(viewLifecycleOwner) { adapter.notify(it.type, it.position) }
            bills.observe(viewLifecycleOwner) {
                updateHeader(
                    binding.lytHeader,
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

    override fun notifyActiveItems(isActive: Boolean) {
        adapter.currentList.forEach { it.isActive = isActive }
        viewModel.setAllBillsActive(isActive)
    }

    override fun notifyItemChanged(position: Int, model: ExpenseModel) {
        viewModel.updateExpense(position, model)
    }

    override fun notifyItemRemoved(position: Int, model: ExpenseModel) {
        viewModel.deleteExpense(position, model)
    }

    companion object {
        fun newInstance() = BillsFragment()
    }

}