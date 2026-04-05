package com.personal.accountantAssistant.ui.expenses

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.InputType
import android.view.View
import androidx.fragment.app.FragmentManager
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.BottomSheetDialogFragment
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.data.enums.ExpensesType.Companion.isBill
import com.personal.accountantAssistant.databinding.FragmentExpensesDetailsBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import java.util.*

class ExpenseDetailsFragment : BottomSheetDialogFragment() {

    override val binding by viewBinding(FragmentExpensesDetailsBinding::inflate)
    val expenseModel by lazy { arguments?.getParcelable<ExpenseModel>(String.ENTITY) }

    var onEditListener: ((model: ExpenseModel) -> Unit)? = null

    override fun initComponents() {
        with(binding) {
            //Title and name
            tvTitle.setText(getActionBarTitle())
            etName.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(expenseModel?.name)
            }
            //Quantity
            etQuantity.apply {
                inputType = InputType.TYPE_NULL
                val quantity = expenseModel?.quantity.orZero()
                setText(AlertDialogBuilder.toCurrentOrMinValue(quantity).toString())
                val dialogBuilder = AlertDialogBuilder(requireContext())
                val dialog = dialogBuilder.setupNumberPickerFrom(quantity) { _, _, value: Int ->
                    setText(AlertDialogBuilder.toCurrentOrMinValue(value).toString())
                }.create()
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus: Boolean ->
                    if (hasFocus) {
                        dialog.show()
                    }
                }
                setOnClickListener { dialog.show() }
            }
            //Date
            lytDate.visibility = if (!isBill(expenseModel?.type))
                View.GONE
            else {
                etDate.apply {
                    inputType = InputType.TYPE_NULL
                    setText(expenseModel?.date.toDateStr())
                    val dialog = AlertDialogBuilder(context)
                    val listener = DatePickerDialog.OnDateSetListener { _, y: Int, m: Int, d: Int ->
                        setText(Calendar.getInstance().also { it[y, m] = d }.time.toDateStr())
                    }
                    setOnClickListener { dialog.showDatePickerFrom(expenseModel?.date, listener) }
                    onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus: Boolean ->
                        if (hasFocus) {
                            dialog.showDatePickerFrom(expenseModel?.date, listener)
                        }
                    }
                }
                View.VISIBLE
            }
            //Value and switch
            etValue.setText(expenseModel?.unitaryValue.toString())
            scActive.isChecked = expenseModel?.isActive.orFalse()
            //Footer
            lytFooter.setFooterLayout()
        }
        setFullScreen()
    }

    override fun getActionBarTitle() = when (expenseModel?.type) {
        ExpensesType.BUY -> R.string.buys_details
        ExpensesType.BILL -> R.string.bills_details
        else -> R.string.app_name
    }

    override fun cancel() {
        dismiss()
    }

    override fun save() {
        binding.apply {
            expenseModel?.update(
                etName.text, etQuantity.text, etDate.text, etValue.text, scActive.isChecked
            )?.let {
                onEditListener?.invoke(it).also { dismiss() }
            }
        }
    }

    override fun initObservers() {}

    companion object {
        fun showDialogFragment(
            model: ExpenseModel, onEdit: (model: ExpenseModel) -> Unit, manager: FragmentManager
        ) {
            ExpenseDetailsFragment().apply {
                arguments = Bundle().apply { putParcelable(String.ENTITY, model) }
                onEditListener = { onEdit(it) }
            }.show(manager, String.EMPTY)
        }
    }
}