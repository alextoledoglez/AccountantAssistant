package com.personal.accountantAssistant.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BarcodeProductResponse(
    val code: String? = null,
    val status: Int? = null,
    @SerialName("status_verbose")
    val statusVerbose: String? = null,
    val product: ProductDto? = null
)

@Serializable
data class ProductDto(
    @SerialName("_id")
    val id: String? = null,
    @SerialName("product_name")
    val productName: String? = null,
    @SerialName("product_name_pt")
    val productNamePt: String? = null,
    val brands: String? = null,
    val categories: String? = null,
    val quantity: String? = null,
    @SerialName("product_quantity")
    val productQuantity: Double? = null,
    @SerialName("product_quantity_unit")
    val productQuantityUnit: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("image_front_url")
    val imageFrontUrl: String? = null,
    @SerialName("image_thumb_url")
    val imageThumbUrl: String? = null,
    @SerialName("ingredients_text")
    val ingredientsText: String? = null,
    val nutriments: NutrimentsDto? = null
)

@Serializable
data class NutrimentsDto(
    @SerialName("energy-kcal_100g")
    val energyKcal100g: Double? = null,
    @SerialName("sugars_100g")
    val sugars100g: Double? = null,
    @SerialName("fat_100g")
    val fat100g: Double? = null,
    @SerialName("proteins_100g")
    val proteins100g: Double? = null
)