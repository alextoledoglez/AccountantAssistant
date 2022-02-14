package com.personal.accountantAssistant.ui.expenses

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.InputType
import android.view.View
import androidx.annotation.RequiresApi
import androidx.viewbinding.ViewBinding
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.BaseBottomSheetDialogFragment
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType.Companion.isBill
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType.Companion.isBuy
import com.personal.accountantAssistant.data.saveDataFrom
import com.personal.accountantAssistant.databinding.FragmentExpensesDetailsBinding
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.DateUtils.toDate
import com.personal.accountantAssistant.utils.DateUtils.toString
import com.personal.accountantAssistant.utils.ToastUtils.showLongText
import kotlinx.android.synthetic.main.fragment_expenses_details.view.*
import kotlinx.android.synthetic.main.options_footer_bar.view.*
import org.koin.android.ext.android.inject
import java.util.*

class ExpenseDetailsFragment : BaseBottomSheetDialogFragment<Nothing>() {

    override val binding: ViewBinding by viewBinding(FragmentExpensesDetailsBinding::inflate)

    val databaseManager: DatabaseManager? by inject()

    lateinit var onSaveActionListener: (expenseEntity: ExpenseEntity) -> Unit

    @RequiresApi(Build.VERSION_CODES.P)
    override fun initView() {
        initializeViewComponentsFrom(getExpense())
        setFullScreen()
    }

    private fun getActionBarTitleFrom(type: ExpensesType?) = when {
        isBuy(type) -> R.string.buys_details
        isBill(type) -> R.string.bills_details
        else -> R.string.app_name
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializeViewComponentsFrom(expenseEntity: ExpenseEntity?) {

        //Title and name
        binding.root.apply {
            tvTitle.setText(getActionBarTitleFrom(expenseEntity?.type))
            etName.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(expenseEntity?.name)
            }
        }

        //Quantity
        binding.root.etQuantity.apply {
            inputType = InputType.TYPE_NULL
            val quantity = expenseEntity?.quantity.orZero()
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
        binding.root.apply {
            lytDate.visibility = if (!isBill(expenseEntity?.type))
                View.GONE
            else {
                etDate.apply {
                    inputType = InputType.TYPE_NULL
                    setText(toString(expenseEntity?.date))
                    val dialog = AlertDialogBuilder(context)
                    val listener = DatePickerDialog.OnDateSetListener { _, y: Int, m: Int, d: Int ->
                        setText(toString(Calendar.getInstance().also { it[y, m] = d }.time))
                    }
                    setOnClickListener { dialog.showDatePickerFrom(expenseEntity?.date, listener) }
                    onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus: Boolean ->
                        if (hasFocus) {
                            dialog.showDatePickerFrom(expenseEntity?.date, listener)
                        }
                    }
                }
                View.VISIBLE
            }
        }

        //Value and switch
        binding.root.apply {
            etValue.setText(java.lang.String.valueOf(expenseEntity?.unitaryValue))
            scActive.isChecked = expenseEntity?.isActive.orFalse()
        }

        //Footer
        binding.root.lytFooter.apply {
            mbCancel.setOnClickListener { dismiss() }
            mbSave.setOnClickListener { save(expenseEntity) }
        }
    }

    private fun getExpense() = (arguments?.getSerializable(Constants.ENTITY) as? ExpenseEntity?)

    @RequiresApi(Build.VERSION_CODES.P)
    private fun save(expenseEntity: ExpenseEntity?) {
        binding.root.apply {
            expenseEntity?.update(
                name = etName.text.toString(),
                quantity = etQuantity.text.toString().toInt(),
                date = toDate(etDate.text.toString()),
                unitaryValue = etValue.text.toString().toDouble(),
                isActive = scActive.isChecked
            )
        }
        databaseManager?.saveDataFrom(context, expenseEntity) {
            showLongText(context, R.string.record_successfully_save)
            expenseEntity?.let { onSaveActionListener.invoke(it) }
            dismiss()
        }
    }

    companion object {
        fun newInstance(expenseEntity: ExpenseEntity?) = ExpenseDetailsFragment().apply {
            arguments = Bundle().apply { putSerializable(Constants.ENTITY, expenseEntity) }
        }
    }
}