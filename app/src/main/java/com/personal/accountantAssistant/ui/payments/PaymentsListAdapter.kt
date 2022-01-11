package com.personal.accountantAssistant.ui.payments

import android.content.Context
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
import com.personal.accountantAssistant.core.extensions.EMPTY
import com.personal.accountantAssistant.core.extensions.orZero
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.data.deleteRecord
import com.personal.accountantAssistant.data.isDefaultRecord
import com.personal.accountantAssistant.data.isNotDefaultRecord
import com.personal.accountantAssistant.ui.MainActivity
import com.personal.accountantAssistant.ui.payments.PaymentsListAdapter.ViewHolderData
import com.personal.accountantAssistant.ui.payments.entities.PaymentsEntity
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType.Companion.isBill
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType.Companion.isBuy
import com.personal.accountantAssistant.utils.CalculatorUtils
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.DateUtils.toString
import com.personal.accountantAssistant.utils.EditableTextsUtils.contains
import com.personal.accountantAssistant.utils.MenuHelper.initializeBillsOptions
import com.personal.accountantAssistant.utils.MenuHelper.initializeBuysOptions
import com.personal.accountantAssistant.utils.NumberUtils.roundTo
import java.util.function.Consumer
import java.util.stream.Collectors

@RequiresApi(Build.VERSION_CODES.P)
class PaymentsListAdapter constructor(
    private val type: PaymentsType,
    private val context: Context?,
    private val databaseManager: DatabaseManager?
) : RecyclerView.Adapter<ViewHolderData>(), Filterable {

    private var payments: MutableList<PaymentsEntity>? = null

    fun loadPayments() =
        databaseManager?.getSortedPaymentsRecordsBy(type) as? MutableList<PaymentsEntity>?

    fun setAllPaymentsRecordsActiveFrom(isActive: Boolean) {
        payments?.forEach(Consumer { payment: PaymentsEntity ->
            setActiveRowFrom(
                isActive,
                payment
            )
        })
    }

    private val layoutToInflate: Int
        get() = if (isBuy(type)) R.layout.buys_item_list else R.layout.bills_item_list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderData {
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutToInflate, null, false)
        return ViewHolderData(view, type)
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun onBindViewHolder(viewHolderData: ViewHolderData, position: Int) {

        val payment = payments?.get(position)
        payment?.let { it ->

            if (isBuy(type) && it.isBuy == true) {
                //INITIALIZE
                initializeBuysOptions()
            }
            if (isBill(type) && it.isBill == true) {
                //INITIALIZE
                initializeBillsOptions()
                viewHolderData.date?.text = toString(it.date)
            }

            //DETAILS
            viewHolderData.apply {
                name.text = it.name
                value.text = toFormattedValue(it)

                //ACTIONS
                active.isChecked = it.isActive
                active.setOnClickListener { _ -> setActiveRowFrom(active.isChecked, it) }
                itemView.setOnClickListener { _ -> editRecordFrom(it) }
                deleteItem.setOnClickListener { _ -> deleteRecordFrom(it) }
            }
            setRowForegroundFrom(viewHolderData)
        }
    }

    private fun setActiveRowFrom(isActive: Boolean, payment: PaymentsEntity) {
        payment.isActive = isActive
        val updateRecord = databaseManager?.updatePaymentsRecordFrom(payment)
        if (databaseManager?.isNotDefaultRecord(updateRecord) == true) {
            notifyPaymentAddedOrChanged(payment)
        }
    }

    private fun editRecordFrom(payment: PaymentsEntity) {
        (context as? MainActivity?)?.supportFragmentManager?.let {
            PaymentsDetailsFragment.newInstance(payment).apply {
                onSaveActionListener = { notifyPaymentAddedOrChanged(payment) }
            }.show(it, String.EMPTY)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private fun deleteRecordFrom(payment: PaymentsEntity) {
        databaseManager?.deleteRecord(context, payment) { notifyPaymentRemoved(payment) }
    }

    private fun setRowForegroundFrom(viewHolderData: ViewHolderData) {
        val color = if (viewHolderData.active.isChecked)
            context?.getColor(R.color.defaultFontColor)
        else
            context?.getColor(R.color.disableForegroundColor)
        color?.let {
            viewHolderData.name.setTextColor(it)
            if (isBill(type)) {
                viewHolderData.date?.setTextColor(it)
            }
            viewHolderData.value.setTextColor(it)
        }
    }

    override fun getItemCount(): Int {
        return payments?.size.orZero()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filterStr = charSequence.toString()
                payments = if (filterStr.isEmpty()) {
                    loadPayments()
                } else {
                    payments?.stream()?.filter { contains(it.name, filterStr) }
                        ?.collect(Collectors.toList())
                }
                return FilterResults().also { it.values = payments }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults?) {
                payments = filterResults?.values as MutableList<PaymentsEntity>?
                notifyDataSetChanged()
            }
        }
    }

    class ViewHolderData(
        itemView: View, paymentsType: PaymentsType?
    ) : RecyclerView.ViewHolder(itemView) {
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
        get() = roundTo(
            payments?.stream()?.filter(PaymentsEntity::isActive)
                ?.map { obj: PaymentsEntity -> obj.getTotalValue() }
                ?.reduce(Constants.DEFAULT_VALUE, CalculatorUtils.accumulatedDoubleSum)
        )

    private fun toFormattedValue(payment: PaymentsEntity): String {
        var quantityStr = java.lang.String.valueOf(payment.quantity)
        quantityStr += if (payment.isBuy == true) Constants.UNITY else Constants.TIMES
        return quantityStr +
                Constants.MULTIPLY_OPERATOR +
                payment.unitaryValue +
                Constants.EQUAL_OPERATOR +
                roundTo(payment.getTotalValue())
    }

    fun notifyPaymentAddedOrChanged(payment: PaymentsEntity) {
        payments = loadPayments()
        val loadedPayment =
            payments?.stream()?.filter { it.equalsTo(payment) }?.findFirst()?.orElse(payment)
        val loadedPaymentId = loadedPayment?.id?.toLong()
        if (databaseManager?.isDefaultRecord(loadedPaymentId) == true) {
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

    private fun notifyPaymentRemoved(payment: PaymentsEntity) = payments?.apply {
        val position = indexOf(payment)
        val wasRemoved = remove(payment)
        if (wasRemoved)
            notifyItemRemoved(position)
    }

    init {
        payments = loadPayments()
    }
}