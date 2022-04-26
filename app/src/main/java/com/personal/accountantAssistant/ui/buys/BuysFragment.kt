package com.personal.accountantAssistant.ui.buys

import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.adapters.BuysListAdapter
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.databinding.FragmentBuysBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.ui.expenses.ExpensesFragment

class BuysFragment : ExpensesFragment<BuysViewModel>() {

    override val binding by viewBinding(FragmentBuysBinding::inflate)
    private val lytSummary by lazy { binding.lytSummary }
    private val lytContent by lazy { binding.lytContent }
    override val adapter by lazy {
        BuysListAdapter(::onEditBuy, viewModel::switchActiveBuy, ::onDeleteBuy)
    }

    override fun onDestroy() {
        super.onDestroy()
        lytContent.rvContent.destroyAdapter()
    }

    override fun initComponents() {
        super.initComponents()
        with(lytSummary) {
            initLayoutSummary(this)
            scActive.setOnClickListener { viewModel.setAllBuysActive(scActive.isChecked) }
        }
        with(lytContent) {
            srlContent.setOnRefreshListener { viewModel.getBuys() }
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
            buys.observe(viewLifecycleOwner) { adapter.submitList(it) { loadSummary() } }
            getBuys()
        }
    }

    override fun importMenuItemClickListener() {
        context?.xlsImport(ExpensesType.BUY)
    }

    override fun exportMenuItemClickListener() {
        //context?.xlsExport(appDatabase, ExpensesType.BUY)
    }

    override fun listAdapterFilterBy(queryStr: String) {
        if (queryStr.isNotBlank())
            adapter.filter.filter(queryStr)
        else
            viewModel.getBuys()
    }

    override fun deleteAllRecords() {
        viewModel.deleteAllBuys()
    }

    private fun onDeleteBuy(model: ExpenseModel) {
        AlertDialogBuilder(requireContext()).showConfirmationFrom(
            R.string.delete_record_title,
            R.string.delete_record_message,
            { viewModel.deleteBuy(model) }
        ) {}
    }

    fun onEditBuy(model: ExpenseModel) {
        ExpenseDetailsFragment.showDialogFragment(
            model, viewModel::saveBuy, requireActivity().supportFragmentManager
        )
    }

    companion object {
        fun newInstance() = BuysFragment()
    }

}