package com.personal.accountantAssistant.ui.payments

import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.InputType
import android.view.View
import androidx.annotation.RequiresApi
import androidx.viewbinding.ViewBinding
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.core.AlertDialogBuilder
import com.personal.accountantAssistant.core.BaseBottomSheetDialogFragment
import com.personal.accountantAssistant.core.extensions.orZero
import com.personal.accountantAssistant.core.extensions.showNumberPickerDialogFrom
import com.personal.accountantAssistant.core.extensions.viewBinding
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.data.saveDataFrom
import com.personal.accountantAssistant.databinding.ActivityPaymentsDetailsBinding
import com.personal.accountantAssistant.ui.payments.entities.Payments
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType.Companion.isBill
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType.Companion.isBuy
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.DatePickerDialogUtils.setDatePickerDialogFrom
import com.personal.accountantAssistant.utils.DateUtils.toDate
import com.personal.accountantAssistant.utils.DateUtils.toString
import com.personal.accountantAssistant.utils.ToastUtils.showLongText
import kotlinx.android.synthetic.main.activity_payments_details.view.*
import kotlinx.android.synthetic.main.options_footer_bar.view.*
import org.koin.android.ext.android.inject

class PaymentsDetailsFragment : BaseBottomSheetDialogFragment<Nothing>() {

    override val binding: ViewBinding by viewBinding(ActivityPaymentsDetailsBinding::inflate)

    val databaseManager: DatabaseManager? by inject()

    lateinit var onSaveActionListener: (payment: Payments) -> Unit

    @RequiresApi(Build.VERSION_CODES.P)
    override fun initView() {
        initializeViewComponentsFrom(getPayment())
        setFullScreen()
    }

    private fun getActionBarTitleFrom(type: PaymentsType?) = when {
        isBuy(type) -> R.string.buys_details
        isBill(type) -> R.string.bills_details
        else -> R.string.app_name
    }

    private fun getDateFieldVisibilityFrom(type: PaymentsType?) = if (isBuy(type))
        View.GONE
    else
        View.VISIBLE

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializeViewComponentsFrom(payment: Payments?) {

        //Title and name
        binding.root.apply {
            tvPaymentDetailsTitle.setText(getActionBarTitleFrom(payment?.type))
            etPaymentName.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(payment?.name)
            }
        }

        //Quantity
        binding.root.etPaymentQuantity.apply {
            inputType = InputType.TYPE_NULL
            val quantity = payment?.quantity.orZero()
            val dialogBuilder = AlertDialogBuilder(requireContext())
            setText(AlertDialogBuilder.toCurrentOrMinTextValue(quantity))
            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus: Boolean ->
                if (hasFocus) {
                    dialogBuilder.showNumberPickerDialogFrom(this, quantity)
                }
            }
            setOnClickListener { dialogBuilder.showNumberPickerDialogFrom(this, quantity) }
        }

        //Date
        binding.root.apply {
            lytDate.visibility = getDateFieldVisibilityFrom(payment?.type)
            etPaymentDate.apply {
                inputType = InputType.TYPE_NULL
                setDatePickerDialogFrom(context, this, toString(payment?.date))
            }
        }

        //Value and switch
        binding.root.apply {
            etPaymentValue.setText(java.lang.String.valueOf(payment?.unitaryValue))
            scActive.isChecked = payment?.isActive ?: false
        }

        //Footer
        binding.root.lytFooter.apply {
            mbCancel.setOnClickListener { dismiss() }
            mbSave.setOnClickListener { savePayment(payment) }
        }
    }

    private fun getPayment() = (arguments?.getSerializable(Constants.ENTITY) as? Payments?)

    @RequiresApi(Build.VERSION_CODES.P)
    private fun savePayment(payment: Payments?) {
        binding.root.apply {
            payment?.update(
                name = etPaymentName.text.toString(),
                quantity = etPaymentQuantity.text.toString().toInt(),
                date = toDate(etPaymentDate.text.toString()),
                unitaryValue = etPaymentValue.text.toString().toDouble(),
                isActive = scActive.isChecked
            )
        }
        databaseManager?.saveDataFrom(context, payment) {
            showLongText(context, R.string.record_successfully_save)
            payment?.let { onSaveActionListener.invoke(it) }
            dismiss()
        }
    }

    companion object {
        fun newInstance(payment: Payments?) = PaymentsDetailsFragment().apply {
            arguments = Bundle().apply { putSerializable(Constants.ENTITY, payment) }
        }
    }
}