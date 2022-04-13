package com.personal.accountantAssistant.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personal.accountantAssistant.data.AppDatabase
import com.personal.accountantAssistant.extensions.DEFAULT_UID
import com.personal.accountantAssistant.extensions.DEFAULT_VALUE
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.ZERO

@Entity(tableName = AppDatabase.EXPENSES_TABLE_NAME)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) var id: Int? = Int.DEFAULT_UID,
    @ColumnInfo(name = NAME) var name: String? = String.EMPTY,
    @ColumnInfo(name = QUANTITY) var quantity: Int? = Int.ZERO,
    @ColumnInfo(name = DATE) var date: String? = String.EMPTY,
    @ColumnInfo(name = UNITARY_VALUE) var unitaryValue: Double? = Double.DEFAULT_VALUE,
    @ColumnInfo(name = TOTAL_VALUE) var totalValue: Double? = Double.DEFAULT_VALUE,
    @ColumnInfo(name = TYPE) var type: String? = String.EMPTY,
    @ColumnInfo(name = ACTIVE) var active: Int? = Int.ZERO
) {
    companion object {
        const val ID = "ID"
        const val NAME = "NAME"
        const val QUANTITY = "QUANTITY"
        const val DATE = "DATE"
        const val UNITARY_VALUE = "UNITARY_VALUE"
        const val TOTAL_VALUE = "TOTAL_VALUE"
        const val TYPE = "TYPE"
        const val ACTIVE = "ACTIVE"
    }
}