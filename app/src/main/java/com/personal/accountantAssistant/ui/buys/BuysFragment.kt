package com.personal.accountantAssistant.ui.buys

import com.personal.accountantAssistant.adapters.BuysListAdapter
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.databinding.FragmentBuysBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.ui.expenses.ExpensesFragment
import com.personal.accountantAssistant.utils.ImportExportUtils
import com.personal.accountantAssistant.utils.MenuHelper

class BuysFragment : ExpensesFragment<BuysViewModel>() {

    override val binding by viewBinding(FragmentBuysBinding::inflate)
    override val adapter by lazy {
        BuysListAdapter(::onEditExpense, viewModel::switchActiveExpense, viewModel::deleteExpense)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvBuys.adapter = null
    }

    override fun initComponents() {
        super.initComponents()
        MenuHelper.initializeBuysOptions()
        binding.srlLoader.setOnRefreshListener { viewModel.getBuys() }
        binding.lytHeader.apply {
            initHeader(this)
            scActive.setOnClickListener { viewModel.setAllBuysActive(scActive.isChecked) }
        }
        binding.rvBuys.adapter = adapter
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) { binding.srlLoader.isRefreshing = it.orFalse() }
            flipper.observe(viewLifecycleOwner) { binding.vfBuys.displayedChild = it.ordinal }
            summary.observe(viewLifecycleOwner) {
                updateHeader(
                    binding.lytHeader,
                    it.isAnyChecked,
                    it.isAllChecked,
                    it.total.toCurrencyMaskedStr()
                )
                binding.srlLoader.stopRefreshing()
            }
            buys.observe(viewLifecycleOwner) { adapter.submitList(it) { loadSummary(it) } }
            getBuys()
        }
    }

    override fun importMenuItemClickListener() {
        ImportExportUtils.xlsImport(context, ExpensesType.BUY)
    }

    override fun exportMenuItemClickListener() {
        // ImportExportUtils.xlsExport(requireContext(), appDatabase, ExpensesType.BUY)
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

    fun onEditExpense(model: ExpenseModel) {
        ExpenseDetailsFragment.newInstance(model).apply {
            onEditListener = { this@BuysFragment.viewModel.saveExpense(model) }
        }.show(requireActivity().supportFragmentManager, String.EMPTY)
    }

    companion object {
        fun newInstance() = BuysFragment()
    }

}