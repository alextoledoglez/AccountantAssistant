package com.personal.accountantAssistant.ui.bills

import com.personal.accountantAssistant.adapters.BillsListAdapter
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.databinding.FragmentBillsBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.ui.expenses.ExpensesFragment

class BillsFragment : ExpensesFragment<BillsViewModel>() {

    override val binding by viewBinding(FragmentBillsBinding::inflate)
    private val lytSummary by lazy { binding.lytSummary }
    private val lytContent by lazy { binding.lytContent }
    override val adapter by lazy {
        BillsListAdapter(::onEditBill, viewModel::switchActiveBill, viewModel::deleteBill)
    }

    override fun onDestroy() {
        super.onDestroy()
        lytContent.rvContent.destroyAdapter()
    }

    override fun initComponents() {
        super.initComponents()
        with(lytSummary) {
            initLayoutSummary(this)
            scActive.setOnClickListener { viewModel.setAllBillsActive(scActive.isChecked) }
        }
        with(lytContent) {
            srlContent.setOnRefreshListener { viewModel.getBills() }
            rvContent.setGridLayoutAdapter(adapter)
        }
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) {
                lytContent.srlContent.updateRefreshing(it.orFalse())
            }
            flipper.observe(viewLifecycleOwner) {
                lytContent.vfContent.updateDisplayedChild(it.ordinal)
            }
            summary.observe(viewLifecycleOwner) {
                updateLayoutSummary(lytSummary, it)
                lytContent.srlContent.stopRefreshing()
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