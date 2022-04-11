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
import com.personal.accountantAssistant.bases.BaseBottomSheetDialogFragment
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.data.enums.ExpensesType.Companion.isBill
import com.personal.accountantAssistant.data.enums.ExpensesType.Companion.isBuy
import com.personal.accountantAssistant.databinding.FragmentExpensesDetailsBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.utils.DateUtils.toString
import java.util.*

class ExpenseDetailsFragment : BaseBottomSheetDialogFragment<Nothing>() {

    override val binding by viewBinding(FragmentExpensesDetailsBinding::inflate)

    var onEditListener: ((model: ExpenseModel) -> Unit)? = null

    override fun initComponents() {
        val model = getExpense()
        with(binding) {
            //Title and name
            tvTitle.setText(getActionBarTitleFrom(model?.type))
            etName.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(model?.name)
            }
            //Quantity
            etQuantity.apply {
                inputType = InputType.TYPE_NULL
                val quantity = model?.quantity.orZero()
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
            lytDate.visibility = if (!isBill(model?.type))
                View.GONE
            else {
                etDate.apply {
                    inputType = InputType.TYPE_NULL
                    setText(toString(model?.date))
                    val dialog = AlertDialogBuilder(context)
                    val listener = DatePickerDialog.OnDateSetListener { _, y: Int, m: Int, d: Int ->
                        setText(toString(Calendar.getInstance().also { it[y, m] = d }.time))
                    }
                    setOnClickListener { dialog.showDatePickerFrom(model?.date, listener) }
                    onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus: Boolean ->
                        if (hasFocus) {
                            dialog.showDatePickerFrom(model?.date, listener)
                        }
                    }
                }
                View.VISIBLE
            }
            //Value and switch
            etValue.setText(model?.unitaryValue.toString())
            scActive.isChecked = model?.isActive.orFalse()
            //Footer
            lytFooter.apply {
                mbCancel.setOnClickListener { dismiss() }
                mbSave.setOnClickListener { save(model) }
            }
        }
        setFullScreen()
    }

    override fun initObservers() {}

    private fun getActionBarTitleFrom(type: ExpensesType?) = when {
        isBuy(type) -> R.string.buys_details
        isBill(type) -> R.string.bills_details
        else -> R.string.app_name
    }

    private fun getExpense() = arguments?.getParcelable<ExpenseModel>(String.ENTITY)

    private fun save(model: ExpenseModel?) {
        model?.let {
            binding.apply {
                it.update(
                    name = etName.text,
                    quantity = etQuantity.text,
                    date = etDate.text,
                    unitaryValue = etValue.text,
                    isActive = scActive.isChecked
                )
                onEditListener?.invoke(model).also { dismiss() }
            }
        }
    }

    companion object {
        fun showDialogFragment(
            model: ExpenseModel,
            onEditExpense: (model: ExpenseModel) -> Unit,
            fragmentManager: FragmentManager
        ) = ExpenseDetailsFragment().apply {
            arguments = Bundle().apply { putParcelable(String.ENTITY, model) }
            onEditListener = { onEditExpense(model) }
        }.show(fragmentManager, String.EMPTY)
    }
}