package com.personal.accountantAssistant.ui.buys

import androidx.compose.runtime.Composable
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.ui.expenses.ExpensesFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class BuysFragment : ExpensesFragment() {

    private val viewModel: BuysViewModel by viewModel()

    override fun initComponents() {
        super.initComponents()
    }

    override fun initObservers() {
        viewModel.loadBuys()
    }

    @Composable
    override fun ScreenContent() {
        BuysScreen(
            viewModel = viewModel,
            onEdit = ::onEditBuy,
            onActive = viewModel::switchActiveBuy,
            onDelete = ::onDeleteBuy
        )
    }

    override fun import() {
        context?.xlsImport(ExpensesType.BUY)
    }

    override fun export() {}

    override fun deleteAll() {
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