package com.personal.accountantAssistant.ui.payments

import com.google.gson.annotations.SerializedName

class CreatePayment {
    @SerializedName("items")
    lateinit var items: Array<Any>
}