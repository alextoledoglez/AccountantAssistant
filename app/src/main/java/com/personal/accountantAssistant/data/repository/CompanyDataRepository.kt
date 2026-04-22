package com.personal.accountantAssistant.data.repository

import com.personal.accountantAssistant.data.mappers.toCompanyModel
import com.personal.accountantAssistant.data.remote.CompanyRemoteDataSource
import com.personal.accountantAssistant.domain.models.CompanyModel
import com.personal.accountantAssistant.domain.repository.CompanyRepository

class CompanyDataRepository(private val dataSource: CompanyRemoteDataSource) : CompanyRepository {

    override fun getCompanies(): List<CompanyModel> {
        return dataSource.loadCompanies().map { it.toCompanyModel() }
    }
}