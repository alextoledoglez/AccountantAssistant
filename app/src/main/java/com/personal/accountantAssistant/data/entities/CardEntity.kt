package com.personal.accountantAssistant.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personal.accountantAssistant.data.AppDatabase
import com.personal.accountantAssistant.extensions.DEFAULT_UID
import com.personal.accountantAssistant.extensions.DEFAULT_VALUE
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.ZERO

@Entity(tableName = AppDatabase.CARDS_TABLE_NAME)
data class CardEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) var id: Int? = Int.DEFAULT_UID,
    @ColumnInfo(name = COMPANY) var company: String? = String.EMPTY,
    @ColumnInfo(name = NAME) var name: String? = String.EMPTY,
    @ColumnInfo(name = PASSWORD) var password: String? = String.EMPTY,
    @ColumnInfo(name = VALUE) var value: Double? = Double.DEFAULT_VALUE,
    @ColumnInfo(name = ACTIVE) var active: Int? = Int.ZERO
) {
    companion object {
        const val ID = "ID"
        const val COMPANY = "COMPANY"
        const val NAME = "NAME"
        const val VALUE = "VALUE"
        const val PASSWORD = "PASSWORD"
        const val ACTIVE = "ACTIVE"
        val FIELDS = listOf(COMPANY, NAME, VALUE, PASSWORD, ACTIVE)
    }
}
