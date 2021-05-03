package com.personal.accountantAssistant.ui.payments

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.ui.payments.PaymentsListAdapter.ViewHolderData
import com.personal.accountantAssistant.ui.payments.entities.Payments
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType.Companion.isBill
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType.Companion.isBuy
import com.personal.accountantAssistant.utils.ActivityUtils.parse
import com.personal.accountantAssistant.utils.CalculatorUtils
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.DataBaseUtils.deleteRecord
import com.personal.accountantAssistant.utils.DataBaseUtils.isDefaultRecord
import com.personal.accountantAssistant.utils.DataBaseUtils.isNotDefaultRecord
import com.personal.accountantAssistant.utils.DateUtils.isInRange
import com.personal.accountantAssistant.utils.DateUtils.toString
import com.personal.accountantAssistant.utils.EditableTextsUtils.contains
import com.personal.accountantAssistant.utils.MenuHelper.initializeBillsOptions
import com.personal.accountantAssistant.utils.MenuHelper.initializeBuysOptions
import com.personal.accountantAssistant.utils.NumberUtils.roundTo
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

@RequiresApi(Build.VERSION_CODES.P)
class PaymentsListAdapter constructor(private val context: Context, private val paymentsType: PaymentsType)
    : RecyclerView.Adapter<ViewHolderData>(), Filterable {

    private var payments: MutableList<Payments>? = null
    private val databaseManager: DatabaseManager = DatabaseManager(context)

    fun loadPayments() {
        payments = databaseManager
                .getPaymentsRecords()
                .stream()
                .filter { it.type == paymentsType }
                .sorted(Comparator.comparing(Payments::date))
                .collect(Collectors.toList())
    }

    fun setAllPaymentsRecordsActiveFrom(isActive: Boolean) {
        payments?.forEach(Consumer { payment: Payments -> setActiveRowFrom(isActive, payment) })
    }

    private val layoutToInflate: Int
        get() = if (isBuy(paymentsType)) R.layout.buys_item_list else R.layout.bills_item_list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderData {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(layoutToInflate, null, java.lang.Boolean.FALSE)
        return ViewHolderData(view, paymentsType)
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun onBindViewHolder(viewHolderData: ViewHolderData, position: Int) {

        payments?.let {

            it[position].let { payment ->

                if (isBuy(paymentsType) && payment.isBuy == true) {
                    //INITIALIZE
                    initializeBuysOptions()
                }
                if (isBill(paymentsType) && payment.isBill == true) {
                    //INITIALIZE
                    initializeBillsOptions()
                    viewHolderData.date?.text = toString(payment.date)
                }

                //DETAILS
                viewHolderData.name.text = payment.name
                viewHolderData.value.text = toFormattedValue(payment)

                //ACTIONS
                viewHolderData.active.isChecked = payment.isActive
                viewHolderData.active.setOnClickListener {
                    val isChecked = viewHolderData.active.isChecked
                    setActiveRowFrom(isChecked, payment)
                }
                viewHolderData.itemView.setOnClickListener { editRecordFrom(payment) }
                viewHolderData.deleteItem.setOnClickListener { deleteRecordFrom(payment) }
                setRowForegroundFrom(viewHolderData)
            }
        }
    }

    private fun setActiveRowFrom(isActive: Boolean, payment: Payments) {
        payment.isActive = isActive
        val updateRecord = databaseManager.updatePaymentsRecordFrom(payment)
        if (isNotDefaultRecord(updateRecord)) {
            notifyDataSetChanged()
        }
    }

    private fun editRecordFrom(payment: Payments) {
        val activity = parse(context)
        val activityIntent = Intent(context, PaymentsDetailsActivity::class.java)
        activityIntent.putExtra(Constants.ENTITY, payment)
        activity.startActivityForResult(activityIntent, Constants.DETAIL_REQUEST_CODE)
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private fun deleteRecordFrom(payment: Payments) {
        deleteRecord(context, payment) {
            payments?.remove(payment)
            notifyDataSetChanged()
        }
    }

    private fun setRowForegroundFrom(viewHolderData: ViewHolderData) {
        val color = if (viewHolderData.active.isChecked)
            context.getColor(R.color.defaultFontColor)
        else
            context.getColor(R.color.disableForegroundColor)
        viewHolderData.name.setTextColor(color)
        if (isBill(paymentsType)) {
            viewHolderData.date?.setTextColor(color)
        }
        viewHolderData.value.setTextColor(color)
    }

    override fun getItemCount(): Int {
        return payments?.size ?: 0
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filterStr = charSequence.toString()
                if (filterStr.isEmpty()) {
                    loadPayments()
                } else {
                    payments = payments?.stream()?.filter { contains(it.name, filterStr) }?.collect(Collectors.toList())
                }
                val filterResults = FilterResults()
                filterResults.values = payments
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults?) {
                payments = filterResults?.values as MutableList<Payments>?
                notifyDataSetChanged()
            }
        }
    }

    class ViewHolderData(itemView: View,
                         paymentsType: PaymentsType?) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name)
        var date: TextView? = null
        var value: TextView

        var active: androidx.appcompat.widget.SwitchCompat
        var deleteItem: ImageButton

        init {

            //DETAILS
            paymentsType?.let {
                if (isBill(it)) {
                    date = itemView.findViewById(R.id.date)
                }
            }
            value = itemView.findViewById(R.id.value)

            //ACTIONS
            active = itemView.findViewById(R.id.active_action)
            deleteItem = itemView.findViewById(R.id.delete_action)
        }
    }

    val totalPrice: Double
        get() {
            val totalPrice = payments?.stream()?.filter(Payments::isActive)
                    ?.map { obj: Payments -> obj.getTotalValue() }
                    ?.reduce(Constants.DEFAULT_VALUE, CalculatorUtils.accumulatedDoubleSum)
            return roundTo(totalPrice)
        }

    fun getTotalPriceUntil(lastPeriodDate: Date?): Double {
        val totalPrice = payments?.stream()?.filter { it.isActive && isInRange(it.date, lastPeriodDate) }
                ?.map { obj: Payments -> obj.getTotalValue() }
                ?.reduce(Constants.DEFAULT_VALUE, CalculatorUtils.accumulatedDoubleSum)
        return roundTo(totalPrice)
    }

    private fun toFormattedValue(payment: Payments): String {
        var quantityStr = java.lang.String.valueOf(payment.quantity)
        quantityStr += if (payment.isBuy == true) Constants.UNITY else Constants.TIMES
        return quantityStr +
                Constants.MULTIPLY_OPERATOR +
                payment.unitaryValue +
                Constants.EQUAL_OPERATOR +
                roundTo(payment.getTotalValue())
    }

    fun notifyItemAddedOrChanged(payment: Payments) {
        loadPayments()
        val loadedPayment = payments?.stream()?.filter { it.equalsTo(payment) }?.findFirst()?.orElse(payment)
        val loadedPaymentId = loadedPayment?.id?.toLong()
        if (isDefaultRecord(loadedPaymentId)) {
            loadedPayment?.let { payments?.add(it) }
            val position = itemCount - 1
            notifyItemInserted(position)
        } else {
            val paymentsSize = itemCount
            payments?.let {
                for (position in 0 until paymentsSize) {
                    val item = it[position]
                    if (loadedPaymentId?.equals(item.id.toLong()) == true) {
                        it[position] = loadedPayment
                        notifyItemChanged(position, loadedPayment)
                        break
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    init {
        loadPayments()
    }
}