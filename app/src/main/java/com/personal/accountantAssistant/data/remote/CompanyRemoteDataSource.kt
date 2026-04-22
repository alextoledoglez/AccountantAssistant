package com.personal.accountantAssistant.data.remote

import android.content.Context
import com.google.gson.reflect.TypeToken
import com.personal.accountantAssistant.data.response.CompanyResponse
import com.personal.accountantAssistant.extensions.fromJson

class CompanyRemoteDataSource(private val context: Context) {
    fun loadCompanies(): List<CompanyResponse> = runCatching {
        val fileName = "catalogs/br/febraban_published_codes.json"
        val raw = context.assets.open(fileName).bufferedReader().use { it.readText() }
        raw.fromJson(typeOfT = object : TypeToken<List<CompanyResponse>>() {})
    }.getOrDefault(defaultValue = emptyList())
}