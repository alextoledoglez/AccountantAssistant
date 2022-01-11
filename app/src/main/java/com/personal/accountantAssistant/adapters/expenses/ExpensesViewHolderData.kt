package com.personal.accountantAssistant.adapters.expenses

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType

class ExpensesViewHolderData(
    itemView: View, expensesType: ExpensesType?
) : RecyclerView.ViewHolder(itemView) {
    var name: TextView = itemView.findViewById(R.id.name)
    var date: TextView? = null
    var value: TextView

    var active: androidx.appcompat.widget.SwitchCompat
    var deleteItem: ImageButton

    init {

        //DETAILS
        expensesType?.let {
            if (ExpensesType.isBill(it)) {
                date = itemView.findViewById(R.id.date)
            }
        }
        value = itemView.findViewById(R.id.value)

        //ACTIONS
        active = itemView.findViewById(R.id.active_action)
        deleteItem = itemView.findViewById(R.id.delete_action)
    }
}