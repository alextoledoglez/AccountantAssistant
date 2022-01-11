package com.personal.accountantAssistant.ui.wallet.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personal.accountantAssistant.utils.Constants
import java.io.Serializable

@Entity
class CardEntity() : Serializable {

    @PrimaryKey
    var id = 0

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "password")
    var password: Int? = 0

    @ColumnInfo(name = "value")
    var value: Double? = Constants.DEFAULT_VALUE

    @ColumnInfo(name = "enabled")
    var isActive: Boolean? = false

    constructor(title: String?) : this() {
        id = 0
        this.title = title
        password = Constants.DEFAULT_QUANTITY_VALUE
        value = Constants.DEFAULT_VALUE
        isActive = Constants.DEFAULT_ACTIVE_STATUS
    }

    fun update(title: String?, password: Int, value: Double, isActive: Boolean) {
        this.title = title
        this.password = password
        this.value = value
        this.isActive = isActive
    }
}
