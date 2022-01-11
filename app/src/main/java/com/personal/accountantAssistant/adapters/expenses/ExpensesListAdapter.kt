package com.personal.accountantAssistant.adapters.expenses

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.data.deleteRecord
import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType.Companion.isBill
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType.Companion.isBuy
import com.personal.accountantAssistant.data.isDefaultRecord
import com.personal.accountantAssistant.data.isNotDefaultRecord
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.ui.MainActivity
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
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
class ExpensesListAdapter constructor(
    private val type: ExpensesType,
    private val context: Context?,
    private val databaseManager: DatabaseManager?
) : RecyclerView.Adapter<ExpensesViewHolderData>(), Filterable {

    private var expens: MutableList<ExpenseEntity>? = null

    fun loadExpenses() =
        databaseManager?.getSortedExpensesRecordsBy(type) as? MutableList<ExpenseEntity>?

    fun setAllExpensesRecordsActiveFrom(isActive: Boolean) {
        expens?.forEach(Consumer { expenseEntity: ExpenseEntity ->
            setActiveRowFrom(isActive, expenseEntity)
        })
    }

    private val layoutToInflate: Int
        get() = if (isBuy(type)) R.layout.buys_item_list else R.layout.bills_item_list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesViewHolderData {
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutToInflate, null, false)
        return ExpensesViewHolderData(view, type)
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun onBindViewHolder(viewHolderData: ExpensesViewHolderData, position: Int) {

        val expenseEntity = expens?.get(position)
        expenseEntity?.let { it ->

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

    private fun setActiveRowFrom(isActive: Boolean, expenseEntity: ExpenseEntity) {
        expenseEntity.isActive = isActive
        val updateRecord = databaseManager?.updateExpenseRecordFrom(expenseEntity)
        if (databaseManager?.isNotDefaultRecord(updateRecord) == true) {
            notifyExpenseAddedOrChanged(expenseEntity)
        }
    }

    private fun editRecordFrom(expenseEntity: ExpenseEntity) {
        (context as? MainActivity?)?.supportFragmentManager?.let {
            ExpenseDetailsFragment.newInstance(expenseEntity).apply {
                onSaveActionListener = { notifyExpenseAddedOrChanged(expenseEntity) }
            }.show(it, String.EMPTY)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private fun deleteRecordFrom(expenseEntity: ExpenseEntity) {
        databaseManager?.deleteRecord(
            context,
            expenseEntity
        ) { notifyExpenseRemoved(expenseEntity) }
    }

    private fun setRowForegroundFrom(viewHolderData: ExpensesViewHolderData) {
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
        return expens?.size.orZero()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filterStr = charSequence.toString()
                expens = if (filterStr.isEmpty()) {
                    loadExpenses()
                } else {
                    expens?.stream()?.filter { contains(it.name, filterStr) }
                        ?.collect(Collectors.toList())
                }
                return FilterResults().also { it.values = expens }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults?) {
                expens = filterResults?.values as MutableList<ExpenseEntity>?
                notifyDataSetChanged()
            }
        }
    }

    val totalPrice: Double
        get() = roundTo(
            expens?.stream()?.filter(ExpenseEntity::isActive)
                ?.map { obj: ExpenseEntity -> obj.getTotalValue() }
                ?.reduce(Constants.DEFAULT_VALUE, CalculatorUtils.accumulatedDoubleSum)
        )

    private fun toFormattedValue(expenseEntity: ExpenseEntity): String {
        var quantityStr = java.lang.String.valueOf(expenseEntity.quantity)
        quantityStr += if (expenseEntity.isBuy == true) Constants.UNITY else Constants.TIMES
        return quantityStr +
                Constants.MULTIPLY_OPERATOR +
                expenseEntity.unitaryValue +
                Constants.EQUAL_OPERATOR +
                roundTo(expenseEntity.getTotalValue())
    }

    fun notifyExpenseAddedOrChanged(expenseEntity: ExpenseEntity) {
        expens = loadExpenses()
        val loadedExpense = expens?.stream()?.filter {
            it.equalsTo(expenseEntity)
        }?.findFirst()?.orElse(expenseEntity)
        val loadedExpenseId = loadedExpense?.id?.toLong()
        if (databaseManager?.isDefaultRecord(loadedExpenseId) == true) {
            loadedExpense?.let { expens?.add(it) }
            val position = itemCount - 1
            notifyItemInserted(position)
        } else {
            expens?.let {
                for (position in 0 until itemCount) {
                    val item = it[position]
                    if (loadedExpenseId?.equals(item.id.toLong()) == true) {
                        it[position] = loadedExpense
                        notifyItemChanged(position, loadedExpense)
                        break
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    private fun notifyExpenseRemoved(expenseEntity: ExpenseEntity) = expens?.apply {
        val position = indexOf(expenseEntity)
        val wasRemoved = remove(expenseEntity)
        if (wasRemoved)
            notifyItemRemoved(position)
    }

    init {
        expens = loadExpenses()
    }
}