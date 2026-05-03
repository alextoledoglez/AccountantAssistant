package com.personal.accountantAssistant.ui.expenses

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentManager
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BottomSheetDialogFragment
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.ENTITY
import com.personal.accountantAssistant.extensions.getParcelableCompat

class ExpenseDetailsFragment : BottomSheetDialogFragment() {

    val expenseModel by lazy { arguments?.getParcelableCompat<ExpenseModel>(String.ENTITY) }

    var onEditListener: ((model: ExpenseModel) -> Unit)? = null

    override fun initComponents() {}

    override fun getActionBarTitle() = when (expenseModel?.type) {
        ExpensesType.BUY -> R.string.buys_details
        ExpensesType.BILL -> R.string.bills_details
        else -> R.string.app_name
    }

    override fun cancel() {
        dismiss()
    }

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
        val TAG: String = ExpenseDetailsFragment::class.java.simpleName
        fun showDialogFragment(
            fragmentManager: FragmentManager?,
            model: ExpenseModel,
            onEdit: (model: ExpenseModel) -> Unit
        ) {
            fragmentManager?.let { manager ->
                ExpenseDetailsFragment().apply {
                    arguments = Bundle().apply { putParcelable(String.ENTITY, model) }
                    onEditListener = { onEdit(it) }
                }.show(manager, String.EMPTY)
            } ?: run {
                Log.w(TAG, "Trying to showDialogFragment with null fragmentManager")
            }
        }
    }
}