package com.personal.accountantAssistant.ui.bills

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.viewbinding.ViewBinding
import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType
import com.personal.accountantAssistant.data.mappers.toBills
import com.personal.accountantAssistant.databinding.FragmentBillsBinding
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.viewBinding
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.ui.expenses.ExpensesFragment
import com.personal.accountantAssistant.utils.ImportExportUtils

class BillsFragment : ExpensesFragment<BillsViewModel>() {

    override val binding: ViewBinding by viewBinding(FragmentBillsBinding::inflate)

    @RequiresApi(Build.VERSION_CODES.P)
    override fun setupView() {
        super.setupView()
        initializeVisualComponentsFrom(binding.root, ExpensesType.BILL)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun addMenuItemClickListener() {
        ExpenseDetailsFragment.newInstance(ExpenseEntity().toBills()).apply {
            onSaveActionListener = { adapter?.notifyExpenseAddedOrChanged(it) }
        }.show(requireActivity().supportFragmentManager, String.EMPTY)
    }

    override fun importMenuItemClickListener() {
        ImportExportUtils.xlsImport(context, ExpensesType.BILL)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun exportMenuItemClickListener() {
        ImportExportUtils.xlsExport(requireContext(), ExpensesType.BILL)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun deleteAllRecords() {
        deleteAllBillsRecords()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun restoreDefaultRecords() {
        deleteAllBillsRecords()?.let {
            databaseManager?.insertDefaultBillsRecords { adapter?.notifyExpenseAddedOrChanged(it) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun deleteAllBillsRecords() = databaseManager?.deleteAllBillsRecord {
        adapter?.notifyCleanExpenses()
    }

    companion object {
        fun newInstance() = BillsFragment()
    }

}