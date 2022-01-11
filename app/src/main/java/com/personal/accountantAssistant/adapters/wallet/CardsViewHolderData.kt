package com.personal.accountantAssistant.adapters.wallet

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R

class CardsViewHolderData(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView = itemView.findViewById(R.id.title)
    var password: TextView = itemView.findViewById(R.id.password)
    var value: TextView = itemView.findViewById(R.id.value)
    var active: androidx.appcompat.widget.SwitchCompat = itemView.findViewById(R.id.scActive)
    var deleteItem: ImageButton = itemView.findViewById(R.id.ibDelete)
}