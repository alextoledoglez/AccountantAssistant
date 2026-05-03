package com.personal.accountantAssistant.domain.useCases.bills

import com.personal.accountantAssistant.domain.repository.CompanyRepository
import com.personal.accountantAssistant.ui.scanner.mappers.SegmentMapper

class GetCompanyNameUseCaseImpl(val repository: CompanyRepository) : GetCompanyNameUseCase {
    override suspend fun invoke(segment: String, company: String): String {
        return repository.getCompanies()
            ?.firstOrNull { it.segmentCode == segment && it.companyCode == company }
            ?.providerName
            ?: SegmentMapper.map(segment)
    }
}