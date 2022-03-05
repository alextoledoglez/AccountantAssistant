package com.personal.accountantAssistant.domain.models.buys

import com.personal.accountantAssistant.extensions.DEFAULT_ACTIVE_STATUS
import com.personal.accountantAssistant.extensions.DEFAULT_QUANTITY_VALUE
import java.math.BigDecimal

data class BuyModel(
    var uid: Int = 0,
    var product: String? = null,
    var quantity: Int = 0,
    var price: BigDecimal = BigDecimal.ZERO,
    var totalValue: BigDecimal = BigDecimal.ZERO,
    var isActive: Boolean = false
) {
    constructor(product: String?) : this() {
        this.product = product
        quantity = Int.DEFAULT_QUANTITY_VALUE
        price = BigDecimal.ZERO
        totalValue = BigDecimal.ZERO
        isActive = Boolean.DEFAULT_ACTIVE_STATUS
    }
}