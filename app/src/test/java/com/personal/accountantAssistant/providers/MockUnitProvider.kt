package com.personal.accountantAssistant.providers

import kotlinx.coroutines.flow.flow

object MockUnitProvider {
    fun mockUnitFlow() = flow { emit(Unit) }
}