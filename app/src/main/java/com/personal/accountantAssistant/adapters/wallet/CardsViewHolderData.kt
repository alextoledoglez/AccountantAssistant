package com.personal.accountantAssistant.adapters.wallet

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R

class CardsViewHolderData(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var tvCompany: TextView = itemView.findViewById(R.id.tvCompany)
    val ivChip: ImageView = itemView.findViewById(R.id.ivChip)
    var tvName: TextView = itemView.findViewById(R.id.tvName)
    var tvValue: TextView = itemView.findViewById(R.id.tvValue)
    var scActive: SwitchCompat = itemView.findViewById(R.id.scActive)
    var ibDelete: ImageButton = itemView.findViewById(R.id.ibDelete)
}