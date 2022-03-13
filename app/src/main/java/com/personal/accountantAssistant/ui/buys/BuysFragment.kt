package com.personal.accountantAssistant.ui.buys

import com.personal.accountantAssistant.adapters.BuysListAdapter
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.databinding.FragmentBuysBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.notify
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr
import com.personal.accountantAssistant.extensions.viewBinding
import com.personal.accountantAssistant.ui.expenses.ExpensesFragment
import com.personal.accountantAssistant.utils.ImportExportUtils
import com.personal.accountantAssistant.utils.MenuHelper

class BuysFragment : ExpensesFragment<BuysViewModel>() {

    override val binding by viewBinding(FragmentBuysBinding::inflate)
    override val adapter by lazy {
        BuysListAdapter(::onExpenseClicked, ::notifyItemChanged, ::notifyItemRemoved)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvBuys.adapter = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.getBuys()
    }

    override fun initComponents() {
        super.initComponents()
        MenuHelper.initializeBuysOptions()
        binding.srlLoader.setOnRefreshListener { viewModel.getBuys() }
        binding.lytHeader.apply {
            initHeader(this)
            scActive.setOnClickListener { notifyActiveItems(scActive.isChecked) }
        }
        binding.rvBuys.adapter = adapter
    }

    override fun initObservers() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) { binding.srlLoader.isRefreshing = it.orFalse() }
            flipper.observe(viewLifecycleOwner) { binding.vfBuys.displayedChild = it.ordinal }
            notify.observe(viewLifecycleOwner) { adapter.notify(it.type, it.position) }
            buys.observe(viewLifecycleOwner) {
                updateHeader(
                    binding.lytHeader,
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

    override fun notifyActiveItems(isActive: Boolean) {
        viewModel.setAllBuysActive(isActive)
    }

    override fun notifyItemChanged(position: Int, model: ExpenseModel) {
        viewModel.updateExpense(position, model)
    }

    override fun notifyItemRemoved(position: Int, model: ExpenseModel) {
        viewModel.deleteExpense(position, model)
    }

    companion object {
        fun newInstance() = BuysFragment()
    }

}