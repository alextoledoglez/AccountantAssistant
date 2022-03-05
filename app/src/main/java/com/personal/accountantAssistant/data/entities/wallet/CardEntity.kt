package com.personal.accountantAssistant.data.entities.wallet

import android.text.Editable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personal.accountantAssistant.extensions.DEFAULT_ACTIVE_STATUS
import com.personal.accountantAssistant.extensions.DEFAULT_QUANTITY_VALUE
import com.personal.accountantAssistant.extensions.toCurrencyBigDecimal
import java.io.Serializable
import java.math.BigDecimal

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
    var value: BigDecimal? = BigDecimal.ZERO

    @ColumnInfo(name = "enabled")
    var isActive: Boolean? = false

    constructor(company: String?, name: String?) : this() {
        id = 0
        this.company = company
        this.name = name
        password = Int.DEFAULT_QUANTITY_VALUE
        value = BigDecimal.ZERO
        isActive = Boolean.DEFAULT_ACTIVE_STATUS
    }

    fun update(
        company: Editable?,
        name: Editable?,
        password: Editable?,
        value: Editable?,
        isActive: Boolean
    ) {
        this.company = company.toString()
        this.name = name.toString()
        this.password = password.toString().toInt()
        this.value = value.toCurrencyBigDecimal()
        this.isActive = isActive
    }

    fun equalsTo(cardEntity: CardEntity): Boolean = (company == cardEntity.company) &&
            (name == cardEntity.name) && (password == cardEntity.password) &&
            (value == cardEntity.value)

}
