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
import com.personal.accountantAssistant.core.BaseBottomSheetDialogFragment
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
import com.personal.accountantAssistant.utils.NumberPickerDialogUtils.initializeFrom
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
                val paymentType = it.type?.name
                tvPaymentDetailsTitle.setText(getActionBarTitleFrom(paymentType))
                etPaymentName.setText(it.name)
                initializeFrom(context, etPaymentQuantity, it.quantity)
                getDateFieldVisibilityFrom(paymentType)?.let { visibility ->
                    lytDate.visibility = visibility
                }
                val dateStr = toString(it.date)
                setDatePickerDialogFrom(context, etPaymentDate, dateStr)
                etPaymentValue.setText(java.lang.String.valueOf(it.unitaryValue))
                payment_active.isChecked = it.isActive
            }

            lytFooter.cancel_button.setOnClickListener { dismiss() }
            lytFooter.save_button.setOnClickListener {
                payment?.let {
                    it.id = it.id
                    it.name = etPaymentName.text.toString()
                    it.quantity = etPaymentQuantity.text.toString().toInt()
                    it.date = toDate(etPaymentDate.text.toString())
                    it.unitaryValue = etPaymentValue.text.toString().toDouble()
                    it.type = it.type?.name?.let { name -> PaymentsType.valueOf(name) }
                    it.isActive = payment_active.isChecked
                }
                databaseManager?.saveDataFrom(activity, payment) {
                    showLongText(activity, R.string.record_successfully_save)
                    onSaveActionListener.invoke()
                    dismiss()
                }
            }

/*        final Button barCodeScanButton = findViewById(R.id.bar_code_scan_button);
        barCodeScanButton.setOnClickListener(v -> {
            //final int REQUEST_CODE = 0;
            final Intent barcodeScanIntent = new Intent(PaymentsDetailsActivity.this, BarcodeScanActivity.class);
            startActivity(barcodeScanIntent);
            //startActivityForResult(barcodeScanIntent, REQUEST_CODE);
            //TODO something
        });*/
        }

        setFullScreen()
    }

    private fun getActionBarTitleFrom(paymentType: String?): Int {
        return paymentType?.let {
            when {
                isBuy(paymentType) -> R.string.buys_details
                isBill(paymentType) -> R.string.bills_details
                else -> R.string.app_name
            }
        } ?: R.string.app_name
    }

    private fun getDateFieldVisibilityFrom(paymentType: String?): Int? {
        return paymentType?.let { if (isBuy(paymentType)) View.GONE else View.VISIBLE }
    }

    companion object {
        fun newInstance(payment: Payments?) = PaymentsDetailsFragment().apply {
            arguments = Bundle().apply { putSerializable(Constants.ENTITY, payment) }
        }
    }
}