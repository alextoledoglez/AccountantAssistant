package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.response.BarcodeProductResponse
import com.personal.accountantAssistant.domain.models.BarcodeProductModel
import com.personal.accountantAssistant.domain.models.Nutrition

fun BarcodeProductResponse.toBarcodeProductModel(): BarcodeProductModel? {
    val product = product ?: return null

    return BarcodeProductModel(
        barcode = code.orEmpty(),
        name = product.productNamePt
            ?: product.productName
            ?: "Produto desconhecido",

        brand = product.brands,
        category = product.categories,
        quantity = product.quantity,
        imageUrl = product.imageFrontUrl ?: product.imageUrl,
        description = product.ingredientsText,

        nutrition = product.nutriments?.let {
            Nutrition(
                calories = it.energyKcal100g,
                sugar = it.sugars100g,
                fat = it.fat100g,
                protein = it.proteins100g
            )
        }
    )
}