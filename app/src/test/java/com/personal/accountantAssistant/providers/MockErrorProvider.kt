package com.personal.accountantAssistant.providers

import com.personal.accountantAssistant.extensions.flowEmit

object MockErrorProvider {
    fun mockErrorFlow() = flowEmit { throw Exception() }
}