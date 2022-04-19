package com.personal.accountantAssistant.ui.buys

import com.personal.accountantAssistant.adapters.BuysListAdapter
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.databinding.FragmentBuysBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.stopRefreshing
import com.personal.accountantAssistant.extensions.viewBinding
import com.personal.accountantAssistant.extensions.xlsImport
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.ui.expenses.ExpensesFragment

class BuysFragment : ExpensesFragment<BuysViewModel>() {

    override val binding by viewBinding(FragmentBuysBinding::inflate)
    override val adapter by lazy {
        BuysListAdapter(::onEditBuy, viewModel::switchActiveBuy, viewModel::deleteBuy)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvBuys.adapter = null
    }

    override fun initComponents() {
        super.initComponents()
        binding.lytSummary.apply {
            initLayoutSummary(this)
            scActive.setOnClickListener { viewModel.setAllBuysActive(scActive.isChecked) }
        }
        binding.srlLoader.setOnRefreshListener { viewModel.getBuys() }
        binding.rvBuys.adapter = adapter
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) { binding.srlLoader.isRefreshing = it.orFalse() }
            flipper.observe(viewLifecycleOwner) { binding.vfBuys.displayedChild = it.ordinal }
            summary.observe(viewLifecycleOwner) {
                updateLayoutSummary(binding.lytSummary, it)
                binding.srlLoader.stopRefreshing()
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

    override fun restoreDefaultRecords() {
        viewModel.setDefaultBuys()
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