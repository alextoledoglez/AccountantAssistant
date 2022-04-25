package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.extensions.flowEmit
import com.personal.accountantAssistant.extensions.toDateStr
import kotlinx.coroutines.flow.Flow
import java.util.*

class SetLastDateUseCaseImpl(private val storage: LocalStorage?) : SetLastDateUseCase {

    override fun invoke(value: Date?): Flow<Unit> = flowEmit {
        storage?.edit()?.putString(LocalStorage.LAST_STR_DATE, value.toDateStr())?.apply()
    }
}