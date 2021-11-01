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

    lateinit var onSaveActionListener: () -> Unit

    @RequiresApi(Build.VERSION_CODES.P)
    override fun initView() {

        val payment = (arguments?.getSerializable(Constants.ENTITY) as? Payments?)

        binding.root.apply {

            etPaymentName.filters = arrayOf<InputFilter>(AllCaps())
            etPaymentQuantity.inputType = InputType.TYPE_NULL
            etPaymentDate.inputType = InputType.TYPE_NULL

            payment?.let { it ->
                val type = it.type?.name

                tvPaymentDetailsTitle.setText(getActionBarTitleFrom(type))
                etPaymentName.setText(it.name)
                initializePaymentQuantityBy(it.quantity)

                getDateFieldVisibilityFrom(type)?.let { visibility ->
                    lytDate.visibility = visibility
                }
                val dateStr = toString(it.date)
                setDatePickerDialogFrom(context, etPaymentDate, dateStr)
                etPaymentValue.setText(java.lang.String.valueOf(it.unitaryValue))
                payment_active.isChecked = it.isActive

                lytFooter.apply {
                    cancel_button.setOnClickListener { dismiss() }
                    save_button.setOnClickListener { _ ->

                        it.id = it.id
                        it.name = etPaymentName.text.toString()
                        it.quantity = etPaymentQuantity.text.toString().toInt()
                        it.date = toDate(etPaymentDate.text.toString())
                        it.unitaryValue = etPaymentValue.text.toString().toDouble()
                        it.type = it.type?.name?.let { name -> PaymentsType.valueOf(name) }
                        it.isActive = payment_active.isChecked

                        databaseManager?.saveDataFrom(activity, it) {
                            showLongText(activity, R.string.record_successfully_save)
                            onSaveActionListener.invoke()
                            dismiss()
                        }
                    }
                }

            }
        }
        setFullScreen()
    }

    private fun getActionBarTitleFrom(paymentType: String?): Int = paymentType?.let {
        when {
            isBuy(paymentType) -> R.string.buys_details
            isBill(paymentType) -> R.string.bills_details
            else -> R.string.app_name
        }
    } ?: R.string.app_name

    private fun getDateFieldVisibilityFrom(paymentType: String?): Int? = paymentType?.let {
        if (isBuy(paymentType)) View.GONE else View.VISIBLE
    }

    private fun initializePaymentQuantityBy(quantity: Int) {
        val dialogBuilder = AlertDialogBuilder(requireContext())
        binding.root.etPaymentQuantity.apply {
            etPaymentQuantity.setText(AlertDialogBuilder.toCurrentOrMinTextValue(quantity))
            setOnClickListener {
                dialogBuilder.showNumberPickerDialogFrom(etPaymentQuantity, quantity)
            }
            onFocusChangeListener = View.OnFocusChangeListener { _: View?, hasFocus: Boolean ->
                if (hasFocus) {
                    dialogBuilder.showNumberPickerDialogFrom(etPaymentQuantity, quantity)
                }
            }
        }
    }

    companion object {
        fun newInstance(payment: Payments?) = PaymentsDetailsFragment().apply {
            arguments = Bundle().apply { putSerializable(Constants.ENTITY, payment) }
        }
    }
}