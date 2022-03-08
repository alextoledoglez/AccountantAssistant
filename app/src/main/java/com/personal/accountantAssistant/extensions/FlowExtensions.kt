package com.personal.accountantAssistant.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

fun <T> flowEmit(block: suspend FlowCollector<T>.() -> T): Flow<T> = flow {
    emit(block())
}