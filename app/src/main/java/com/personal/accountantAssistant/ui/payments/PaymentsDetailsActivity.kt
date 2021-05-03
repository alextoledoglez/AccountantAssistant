package com.personal.accountantAssistant.ui.payments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.ui.payments.entities.Payments
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType.Companion.isBill
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType.Companion.isBuy
import com.personal.accountantAssistant.utils.*
import com.personal.accountantAssistant.utils.DataBaseUtils.saveDataFrom
import com.personal.accountantAssistant.utils.DatePickerDialogUtils.setDatePickerDialogFrom
import com.personal.accountantAssistant.utils.DateUtils.toDate
import com.personal.accountantAssistant.utils.DateUtils.toString
import com.personal.accountantAssistant.utils.NumberPickerDialogUtils.initializeFrom
import com.personal.accountantAssistant.utils.ParserUtils.isNotNullObject
import com.personal.accountantAssistant.utils.ParserUtils.toPayments
import com.personal.accountantAssistant.utils.ToastUtils.showLongText
import java.util.*

class PaymentsDetailsActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val payment: Payments?
        val intent = intent
        val context: Context = this@PaymentsDetailsActivity
        val activity: Activity = this@PaymentsDetailsActivity
        payment = if (isNotNullObject(intent)) {
            val entity: Any? = intent.getSerializableExtra(Constants.ENTITY)
            toPayments(entity)
        } else {
            null
        }
        setContentView(R.layout.activity_payments_details)
        val nameEditText = findViewById<EditText>(R.id.payment_name)
        nameEditText.filters = arrayOf<InputFilter>(AllCaps())
        val quantityEditText = findViewById<EditText>(R.id.payment_quantity)
        quantityEditText.inputType = InputType.TYPE_NULL
        val dateTextView = findViewById<TextView>(R.id.payment_date_label)
        val dateEditText = findViewById<EditText>(R.id.payment_date)
        dateEditText.inputType = InputType.TYPE_NULL
        val valueEditText = findViewById<EditText>(R.id.payment_value)
        val activeSwitch = findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.payment_active)
        payment?.let { it ->
            val paymentType = it.type?.name
            supportActionBar?.setTitle(getActionBarTitleFrom(paymentType))
            nameEditText.setText(it.name)
            initializeFrom(context, quantityEditText, it.quantity)
            getDateFieldVisibilityFrom(paymentType)?.let { visibility ->
                dateTextView.visibility = visibility
                dateEditText.visibility = visibility
            }
            val dateStr = toString(it.date)
            setDatePickerDialogFrom(context, dateEditText, dateStr)
            valueEditText.setText(java.lang.String.valueOf(it.unitaryValue))
            activeSwitch.isChecked = it.isActive
        }
        val cancelBtn = findViewById<Button>(R.id.cancel_button)
        cancelBtn.setOnClickListener { finish() }
        val saveBtn = findViewById<Button>(R.id.save_button)
        saveBtn.setOnClickListener {
            payment?.let {
                it.id = it.id
                it.name = nameEditText.text.toString()
                it.quantity = quantityEditText.text.toString().toInt()
                it.date = toDate(dateEditText.text.toString())
                it.unitaryValue = valueEditText.text.toString().toDouble()
                it.type = it.type?.name?.let { name -> PaymentsType.valueOf(name) }
                it.isActive = activeSwitch.isChecked
            }
            saveDataFrom(activity, payment) {
                val resultIntent = Intent()
                resultIntent.putExtra(Constants.ENTITY, payment)
                setResult(RESULT_OK, resultIntent)
                finish()
                showLongText(activity, R.string.record_successfully_save)
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
}