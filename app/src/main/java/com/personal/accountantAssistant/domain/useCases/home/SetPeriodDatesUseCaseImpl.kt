package com.personal.accountantAssistant.domain.useCases.home

import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.extensions.flowEmit
import com.personal.accountantAssistant.extensions.toDateStr
import kotlinx.coroutines.flow.Flow
import java.util.*

class SetPeriodDatesUseCaseImpl(private val storage: LocalStorage?) : SetPeriodDatesUseCase {

    override fun invoke(first: Date?, last: Date?): Flow<Unit> = flowEmit {
        storage?.edit()?.apply {
            putString(LocalStorage.FIRST_STR_DATE, first.toDateStr())
            putString(LocalStorage.LAST_STR_DATE, last.toDateStr())
        }?.apply()
    }
}