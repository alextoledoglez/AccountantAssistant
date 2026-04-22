package com.personal.accountantAssistant.domain.useCases.bills

interface GetCompanyNameUseCase {
    operator fun invoke(segment: String, company: String): String
}