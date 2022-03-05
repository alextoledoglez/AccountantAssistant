package com.personal.accountantAssistant.adapters.expenses

import android.annotation.SuppressLint
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
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.ui.MainActivity
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.utils.CalculatorUtils
import com.personal.accountantAssistant.utils.DateUtils.toString
import com.personal.accountantAssistant.utils.EditableTextsUtils.contains
import com.personal.accountantAssistant.utils.MenuHelper.initializeBillsOptions
import com.personal.accountantAssistant.utils.MenuHelper.initializeBuysOptions
import java.math.BigDecimal
import java.util.function.Consumer
import java.util.stream.Collectors

@RequiresApi(Build.VERSION_CODES.P)
class ExpensesListAdapter constructor(
    private val type: ExpensesType,
    private val context: Context?,
    private val databaseManager: DatabaseManager?
) : RecyclerView.Adapter<ExpensesViewHolderData>(), Filterable {

    private var expenses: MutableList<ExpenseEntity>? = null

    fun loadExpenses() =
        databaseManager?.getSortedExpensesRecordsBy(type) as? MutableList<ExpenseEntity>?

    fun setAllExpensesRecordsActiveFrom(isActive: Boolean) {
        expenses?.forEach(Consumer { expenseEntity: ExpenseEntity ->
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

        val expenseEntity = expenses?.get(position)
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
            context?.getColor(R.color.fontColor)
        else
            context?.getColor(R.color.disableFontColor)
        color?.let {
            viewHolderData.name.setTextColor(it)
            if (isBill(type)) {
                viewHolderData.date?.setTextColor(it)
            }
            viewHolderData.value.setTextColor(it)
        }
    }

    override fun getItemCount(): Int {
        return expenses?.size.orZero()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filterStr = charSequence.toString()
                expenses = if (filterStr.isEmpty()) {
                    loadExpenses()
                } else {
                    expenses?.stream()?.filter { contains(it.name, filterStr) }
                        ?.collect(Collectors.toList())
                }
                return FilterResults().also { it.values = expenses }
            }

            @Suppress("UNCHECKED_CAST")
            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults?) {
                expenses = filterResults?.values as MutableList<ExpenseEntity>?
                notifyDataSetChanged()
            }
        }
    }

    val totalPrice: BigDecimal
        get() = expenses?.stream()?.filter(ExpenseEntity::isActive)
            ?.map { obj: ExpenseEntity -> obj.getTotalValue() }
            ?.reduce(BigDecimal.ZERO, CalculatorUtils.accumulatedDecimalSum).orZero().rounded()

    private fun toFormattedValue(entity: ExpenseEntity): String {
        val isBuyEntity = entity.isBuy.orFalse()
        var quantityStr = entity.quantity.toString()
        val operator = if (isBuyEntity) {
            quantityStr += String.UNITY
            String.MULTIPLY_OPERATOR
        } else {
            quantityStr += String.TIMES
            String.EMPTY
        }
        val unitaryPriceStr = entity.unitaryValue.toCurrencyMaskedStr()
        val totalPriceStr = entity.getTotalValue().toCurrencyMaskedStr()
        return "$quantityStr$operator${unitaryPriceStr}${String.EQUAL_OPERATOR}${totalPriceStr}"
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyExpenseAddedOrChanged(expenseEntity: ExpenseEntity) {
        expenses = loadExpenses()
        val loadedExpense = expenses?.stream()?.filter {
            it.equalsTo(expenseEntity)
        }?.findFirst()?.orElse(expenseEntity)
        val loadedExpenseId = loadedExpense?.id?.toLong()
        if (databaseManager?.isDefaultRecord(loadedExpenseId) == true) {
            loadedExpense?.let { expenses?.add(it) }
            val position = itemCount - 1
            notifyItemInserted(position)
        } else {
            expenses?.let {
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

    private fun notifyExpenseRemoved(expenseEntity: ExpenseEntity) = expenses?.apply {
        val position = indexOf(expenseEntity)
        val wasRemoved = remove(expenseEntity)
        if (wasRemoved)
            notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyCleanExpenses() {
        expenses = mutableListOf()
        notifyDataSetChanged()
    }

    init {
        expenses = loadExpenses()
    }
}