package com.personal.accountantAssistant.ui.bills

import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.adapters.BillsListAdapter
import com.personal.accountantAssistant.bases.AlertDialogBuilder
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
    private val srlContent by lazy { lytContent.srlContent }
    private val vfContent by lazy { lytContent.vfContent }
    private val rvContent by lazy { lytContent.rvContent }

    override val adapter by lazy {
        BillsListAdapter(::onEditBill, viewModel::switchActiveBill, ::onDeleteBill)
    }

    override fun onDestroy() {
        super.onDestroy()
        rvContent.destroyAdapter()
    }

    override fun initComponents() {
        super.initComponents()
        with(lytSummary) {
            initLayoutSummary(this)
            scActive.setOnClickListener { viewModel.setAllBillsActive(scActive.isChecked) }
        }
        srlContent.setOnRefreshListener { viewModel.getBills() }
        rvContent.setGridLayoutAdapter(adapter)
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) { srlContent.updateRefreshing(it.orFalse()) }
            flipper.observe(viewLifecycleOwner) { vfContent.updateDisplayedChild(it.ordinal) }
            summary.observe(viewLifecycleOwner) {
                updateLayoutSummary(lytSummary, it)
                srlContent.stopRefreshing()
            }
            bills.observe(viewLifecycleOwner) {
                adapter.submitList(it) { loadSummary() }
                srlContent.stopRefreshing()
                rvContent.scrollToTop()
            }
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

    private fun onDeleteBill(model: ExpenseModel) {
        AlertDialogBuilder(requireContext()).showConfirmationFrom(
            R.string.delete_record_title,
            R.string.delete_record_message,
            { viewModel.deleteBill(model) }
        ) {}
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