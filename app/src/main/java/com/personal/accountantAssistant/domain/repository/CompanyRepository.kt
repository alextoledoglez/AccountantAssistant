package com.personal.accountantAssistant.domain.repository

import com.personal.accountantAssistant.domain.models.CompanyModel

interface CompanyRepository {
    fun getCompanies(): List<CompanyModel>?
}