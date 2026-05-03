package com.personal.accountantAssistant.data.response

import kotlinx.serialization.Serializable

@Serializable
data class CompanyResponse(
    val segmentCode: String,
    val segmentName: String,
    val companyCode: String,
    val providerName: String,
    val uf: String
)
