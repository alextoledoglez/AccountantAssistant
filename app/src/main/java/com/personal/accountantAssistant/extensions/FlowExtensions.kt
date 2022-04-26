package com.personal.accountantAssistant.extensions

import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

fun <T> flowEmit(block: suspend FlowCollector<T>.() -> T) = flow {
    emit(block())
}

fun <T> flowEmitOn(context: CoroutineContext, block: suspend FlowCollector<T>.() -> T) = flow {
    emit(block())
}.flowOn(context)

fun <T> Flow<T>.onError(action: suspend FlowCollector<T>.(cause: Throwable) -> Unit) = catch {
    action.invoke(this, it)
}