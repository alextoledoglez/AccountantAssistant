package com.personal.accountantAssistant.domain.useCases.bills

interface GetCompanyNameUseCase {
    suspend operator fun invoke(segment: String, company: String): String
}