package com.personal.accountantAssistant.ui.bills

import androidx.compose.runtime.Composable
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.ui.expenses.ExpensesFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class BillsFragment : ExpensesFragment() {

    private val viewModel: BillsViewModel by viewModel()

    override fun initComponents() {
        super.initComponents()
    }

    override fun initObservers() {
        viewModel.loadBills()
    }

    @Composable
    override fun ScreenContent() {
        BillsScreen(
            viewModel = viewModel,
            onEdit = ::onEditBill,
            onActive = viewModel::switchActiveBill,
            onDelete = ::onDeleteBill
        )
    }

    override fun import() {
        context?.xlsImport(ExpensesType.BILL)
    }

    override fun export() {}

    override fun deleteAll() {
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