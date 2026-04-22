package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.response.CompanyResponse
import com.personal.accountantAssistant.domain.models.CompanyModel

fun CompanyResponse.toCompanyModel() = CompanyModel(
    segmentCode = segmentCode,
    segmentName = segmentName,
    companyCode = companyCode,
    providerName = providerName,
    uf = uf
)