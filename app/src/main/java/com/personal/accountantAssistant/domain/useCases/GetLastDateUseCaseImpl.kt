package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.ONE
import com.personal.accountantAssistant.extensions.flowEmit
import com.personal.accountantAssistant.extensions.toDate
import kotlinx.coroutines.flow.Flow
import java.util.*

class GetLastDateUseCaseImpl(private val storage: LocalStorage?) : GetLastDateUseCase {

    override fun invoke(): Flow<Date?> = flowEmit {
        storage?.getString(LocalStorage.LAST_STR_DATE, String.EMPTY)?.toDate() ?: run {
            val nextMonth = Calendar.getInstance()
            nextMonth.add(Calendar.MONTH, Int.ONE)
            nextMonth.time
        }
    }
}