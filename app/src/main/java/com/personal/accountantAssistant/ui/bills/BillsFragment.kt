package com.personal.accountantAssistant.ui.bills

import com.personal.accountantAssistant.adapters.BillsListAdapter
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.databinding.FragmentBillsBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.ui.expenses.ExpensesFragment
import com.personal.accountantAssistant.utils.ImportExportUtils
import com.personal.accountantAssistant.utils.MenuHelper

class BillsFragment : ExpensesFragment<BillsViewModel>() {

    override val binding by viewBinding(FragmentBillsBinding::inflate)
    override val adapter by lazy {
        BillsListAdapter(::onEditExpense, viewModel::switchActiveExpense, viewModel::deleteExpense)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvBills.adapter = null
    }

    override fun initComponents() {
        super.initComponents()
        MenuHelper.initializeBillsOptions()
        binding.srlLoader.setOnRefreshListener { viewModel.getBills() }
        binding.lytHeader.apply {
            initHeader(this)
            scActive.setOnClickListener { viewModel.setAllBillsActive(scActive.isChecked) }
        }
        binding.rvBills.adapter = adapter
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) { binding.srlLoader.isRefreshing = it.orFalse() }
            flipper.observe(viewLifecycleOwner) { binding.vfBills.displayedChild = it.ordinal }
            summary.observe(viewLifecycleOwner) {
                updateHeader(
                    binding.lytHeader,
                    it.isAnyChecked,
                    it.isAllChecked,
                    it.total.toCurrencyMaskedStr()
                )
                binding.srlLoader.stopRefreshing()
            }
            bills.observe(viewLifecycleOwner) { adapter.submitList(it) { loadSummary(it) } }
            getBills()
        }
    }

    override fun importMenuItemClickListener() {
        ImportExportUtils.xlsImport(context, ExpensesType.BILL)
    }

    override fun exportMenuItemClickListener() {
        //ImportExportUtils.xlsExport(requireContext(), appDatabase, ExpensesType.BILL)
    }

    override fun listAdapterFilterBy(queryStr: String) {
        if (queryStr.isNotBlank())
            adapter.filter.filter(queryStr)
        else
            viewModel.getBills()
    }

    override fun deleteAllRecords() {
        viewModel.deleteAllBills()
    }

    override fun restoreDefaultRecords() {
        viewModel.setDefaultBills()
    }

    fun onEditExpense(model: ExpenseModel) {
        ExpenseDetailsFragment.newInstance(model).apply {
            onEditListener = { this@BillsFragment.viewModel.saveExpense(model) }
        }.show(requireActivity().supportFragmentManager, String.EMPTY)
    }

    companion object {
        fun newInstance() = BillsFragment()
    }

}