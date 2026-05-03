package com.personal.accountantAssistant.domain.models

data class BarcodeProductModel(
    val barcode: String,
    val name: String,
    val brand: String?,
    val category: String?,
    val quantity: String?,
    val imageUrl: String?,
    val description: String?,
    val nutrition: Nutrition?
)

data class Nutrition(
    val calories: Double?,
    val sugar: Double?,
    val fat: Double?,
    val protein: Double?
)
