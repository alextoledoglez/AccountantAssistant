package com.personal.accountantAssistant.ui.buys

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.viewbinding.ViewBinding
import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType
import com.personal.accountantAssistant.data.isNotDefaultRecord
import com.personal.accountantAssistant.data.mappers.toBuys
import com.personal.accountantAssistant.databinding.FragmentBuysBinding
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.viewBinding
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.ui.expenses.ExpensesFragment
import com.personal.accountantAssistant.utils.ActionUtils
import com.personal.accountantAssistant.utils.ImportExportUtils

class BuysFragment : ExpensesFragment<BuysViewModel>() {

    override val binding: ViewBinding by viewBinding(FragmentBuysBinding::inflate)

    @RequiresApi(Build.VERSION_CODES.P)
    override fun setupView() {
        super.setupView()
        initializeVisualComponentsFrom(binding.root, ExpensesType.BUY)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun addMenuItemClickListener() {
        ExpenseDetailsFragment.newInstance(ExpenseEntity().toBuys()).apply {
            onSaveActionListener = { adapter?.notifyExpenseAddedOrChanged(it) }
        }.show(requireActivity().supportFragmentManager, String.EMPTY)
    }

    override fun importMenuItemClickListener() {
        ImportExportUtils.xlsImport(context, ExpensesType.BUY)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun exportMenuItemClickListener() {
        ImportExportUtils.xlsExport(requireContext(), ExpensesType.BUY)
    }

    override fun deleteAllRecords() {
        deleteAllBuysRecords()?.let {
            ActionUtils.conditionalActions(it, { /*refreshRecyclerView()*/ })
        }
    }

    override fun restoreDefaultRecords() {
        deleteAllBuysRecords()?.let {
            databaseManager?.insertDefaultBuysRecords()
            //refreshRecyclerView()
        }
    }

    private fun deleteAllBuysRecords(): Boolean? {
        return databaseManager?.deleteAllBuysRecord()
            ?.let { databaseManager?.isNotDefaultRecord(it) }
    }

    companion object {
        fun newInstance() = BuysFragment()
    }

}