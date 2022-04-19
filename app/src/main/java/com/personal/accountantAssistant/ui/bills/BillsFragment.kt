package com.personal.accountantAssistant.ui.bills

import com.personal.accountantAssistant.adapters.BillsListAdapter
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.databinding.FragmentBillsBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.stopRefreshing
import com.personal.accountantAssistant.extensions.viewBinding
import com.personal.accountantAssistant.extensions.xlsImport
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.ui.expenses.ExpensesFragment

class BillsFragment : ExpensesFragment<BillsViewModel>() {

    override val binding by viewBinding(FragmentBillsBinding::inflate)
    override val adapter by lazy {
        BillsListAdapter(::onEditBill, viewModel::switchActiveBill, viewModel::deleteBill)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvBills.adapter = null
    }

    override fun initComponents() {
        super.initComponents()
        binding.srlLoader.setOnRefreshListener { viewModel.getBills() }
        binding.lytSummary.apply {
            initLayoutSummary(this)
            scActive.setOnClickListener { viewModel.setAllBillsActive(scActive.isChecked) }
        }
        binding.rvBills.adapter = adapter
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) { binding.srlLoader.isRefreshing = it.orFalse() }
            flipper.observe(viewLifecycleOwner) { binding.vfBills.displayedChild = it.ordinal }
            summary.observe(viewLifecycleOwner) {
                updateLayoutSummary(binding.lytSummary, it)
                binding.srlLoader.stopRefreshing()
            }
            bills.observe(viewLifecycleOwner) { adapter.submitList(it) { loadSummary() } }
            getBills()
        }
    }

    override fun importMenuItemClickListener() {
        context?.xlsImport(ExpensesType.BILL)
    }

    override fun exportMenuItemClickListener() {
        //context?.xlsExport(appDatabase, ExpensesType.BILL)
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

    fun onEditBill(model: ExpenseModel) {
        ExpenseDetailsFragment.showDialogFragment(
            model, viewModel::saveBill, requireActivity().supportFragmentManager
        )
    }

    companion object {
        fun newInstance() = BillsFragment()
    }

}