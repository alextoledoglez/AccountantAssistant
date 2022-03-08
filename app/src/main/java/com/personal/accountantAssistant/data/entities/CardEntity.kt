package com.personal.accountantAssistant.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personal.accountantAssistant.data.AppDatabase.Companion.CARD_TABLE
import com.personal.accountantAssistant.extensions.DEFAULT_UID
import com.personal.accountantAssistant.extensions.EMPTY

@Entity(tableName = CARD_TABLE)
data class CardEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "ID") var id: Int? = Int.DEFAULT_UID,
    @ColumnInfo(name = "COMPANY") var company: String? = String.EMPTY,
    @ColumnInfo(name = "NAME") var name: String? = String.EMPTY,
    @ColumnInfo(name = "PASSWORD") var password: String? = String.EMPTY,
    @ColumnInfo(name = "VALUE") var value: String? = String.EMPTY,
    @ColumnInfo(name = "ACTIVE") var isActive: String? = String.EMPTY
) {
    constructor(company: String?, name: String?) : this() {
        this.company = company.orEmpty()
        this.name = name.orEmpty()
    }
}
