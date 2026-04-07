package com.personal.accountantAssistant.ui.expenses

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentManager
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BottomSheetDialogFragment
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*

class ExpenseDetailsFragment : BottomSheetDialogFragment() {

    val expenseModel by lazy { arguments?.getParcelable<ExpenseModel>(String.ENTITY) }

    var onEditListener: ((model: ExpenseModel) -> Unit)? = null

    override fun initComponents() {}

    override fun getActionBarTitle() = when (expenseModel?.type) {
        com.personal.accountantAssistant.data.enums.ExpensesType.BUY -> R.string.buys_details
        com.personal.accountantAssistant.data.enums.ExpensesType.BILL -> R.string.bills_details
        else -> R.string.app_name
    }

    override fun cancel() { dismiss() }

    override fun save() {}

    override fun initObservers() {}

    @Composable
    override fun ScreenContent() {
        ExpenseDetailsScreen(
            expenseModel = expenseModel,
            onSave = { model ->
                onEditListener?.invoke(model)
                dismiss()
            },
            onCancel = ::cancel
        )
    }

    companion object {
        fun showDialogFragment(
            model: ExpenseModel,
            onEdit: (model: ExpenseModel) -> Unit,
            manager: FragmentManager
        ) {
            ExpenseDetailsFragment().apply {
                arguments = Bundle().apply { putParcelable(String.ENTITY, model) }
                onEditListener = { onEdit(it) }
            }.show(manager, String.EMPTY)
        }
    }
}