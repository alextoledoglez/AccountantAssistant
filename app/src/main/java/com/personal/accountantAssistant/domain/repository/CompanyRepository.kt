package com.personal.accountantAssistant.domain.repository

import com.personal.accountantAssistant.domain.models.CompanyModel

interface CompanyRepository {
    suspend fun getCompanies(): List<CompanyModel>?
}