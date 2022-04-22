package com.personal.accountantAssistant.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

fun <T> flowEmit(block: suspend FlowCollector<T>.() -> T) = flow {
    emit(block())
}

fun <T> Flow<T>.onError(action: suspend FlowCollector<T>.(cause: Throwable) -> Unit) = catch {
    action.invoke(this, it)
}