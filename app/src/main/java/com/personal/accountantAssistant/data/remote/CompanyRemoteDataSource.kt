package com.personal.accountantAssistant.data.remote

import com.google.firebase.database.FirebaseDatabase
import com.personal.accountantAssistant.data.response.CompanyResponse
import kotlinx.coroutines.tasks.await

class CompanyRemoteDataSource(private val database: FirebaseDatabase) {
    suspend fun loadCompanies(): List<CompanyResponse> = runCatching {
        database.getReference("febraban_published_codes").get().await().children.mapNotNull {
            CompanyResponse(
                segmentCode = it.child("segmentCode").getValue(String::class.java).orEmpty(),
                segmentName = it.child("segmentName").getValue(String::class.java).orEmpty(),
                companyCode = it.child("companyCode").getValue(String::class.java).orEmpty(),
                providerName = it.child("providerName").getValue(String::class.java).orEmpty(),
                uf = it.child("uf").getValue(String::class.java).orEmpty()
            )
        }
    }.getOrDefault(emptyList())
}