package com.personal.accountantAssistant.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personal.accountantAssistant.data.AppDatabase.Companion.PAYMENTS_TABLE
import com.personal.accountantAssistant.extensions.DEFAULT_UID
import com.personal.accountantAssistant.extensions.EMPTY

@Entity(tableName = PAYMENTS_TABLE)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "ID") var id: Int? = Int.DEFAULT_UID,
    @ColumnInfo(name = "NAME") var name: String? = String.EMPTY,
    @ColumnInfo(name = "QUANTITY") var quantity: String? = String.EMPTY,
    @ColumnInfo(name = "DATE") var date: String? = String.EMPTY,
    @ColumnInfo(name = "UNITARY_VALUE") var unitaryValue: String? = String.EMPTY,
    @ColumnInfo(name = "TOTAL_VALUE") var totalValue: String? = String.EMPTY,
    @ColumnInfo(name = "TYPE") var type: String? = String.EMPTY,
    @ColumnInfo(name = "ACTIVE") var isActive: String? = String.EMPTY
)