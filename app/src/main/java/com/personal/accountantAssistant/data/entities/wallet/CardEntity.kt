package com.personal.accountantAssistant.data.entities.wallet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personal.accountantAssistant.extensions.DEFAULT_ACTIVE_STATUS
import com.personal.accountantAssistant.extensions.DEFAULT_QUANTITY_VALUE
import com.personal.accountantAssistant.extensions.DEFAULT_VALUE
import java.io.Serializable

@Entity
class CardEntity() : Serializable {

    @PrimaryKey
    var id = 0

    @ColumnInfo(name = "title")
    var company: String? = null

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "password")
    var password: Int? = 0

    @ColumnInfo(name = "value")
    var value: Double? = Double.DEFAULT_VALUE

    @ColumnInfo(name = "enabled")
    var isActive: Boolean? = false

    constructor(company: String?, name: String?) : this() {
        id = 0
        this.company = company
        this.name = name
        password = Int.DEFAULT_QUANTITY_VALUE
        value = Double.DEFAULT_VALUE
        isActive = Boolean.DEFAULT_ACTIVE_STATUS
    }

    fun update(
        company: String?, name: String?, password: Int, value: Double, isActive: Boolean
    ) {
        this.company = company
        this.name = name
        this.password = password
        this.value = value
        this.isActive = isActive
    }

    fun equalsTo(cardEntity: CardEntity): Boolean = (company == cardEntity.company) &&
            (name == cardEntity.name) && (password == cardEntity.password) &&
            (value == cardEntity.value)

}
