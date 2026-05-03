package com.personal.accountantAssistant.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.reflect.KClass

internal fun <T : Any> T.toJson() = Gson().toJson(this)

internal fun <T : Any> String.fromJson(clazz: KClass<T>): T = Gson().fromJson(this, clazz.java)
internal fun <T : Any> String.fromJson(typeOfT: TypeToken<T>): T = Gson().fromJson(this, typeOfT)