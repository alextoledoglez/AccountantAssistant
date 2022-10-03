package com.personal.accountantAssistant.bases

import android.content.SharedPreferences
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.fromJson
import com.personal.accountantAssistant.extensions.takeIfNotBlank
import com.personal.accountantAssistant.extensions.toJson
import kotlin.reflect.KClass

abstract class BasePreferences(private val preferences: SharedPreferences) : SharedPreferences {

    override fun getAll(): MutableMap<String, *> = preferences.all

    override fun getString(key: String?, default: String?) = preferences.getString(key, default)

    override fun getStringSet(
        key: String?, default: MutableSet<String>?
    ): MutableSet<String>? = preferences.getStringSet(key, default)

    override fun getInt(key: String?, default: Int) = preferences.getInt(key, default)

    override fun getLong(key: String?, default: Long) = preferences.getLong(key, default)

    override fun getFloat(key: String?, default: Float) = preferences.getFloat(key, default)

    override fun getBoolean(key: String?, default: Boolean) = preferences.getBoolean(key, default)

    override fun contains(value: String?): Boolean = preferences.contains(value)

    override fun edit(): SharedPreferences.Editor = preferences.edit()

    override fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?
    ) {
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?
    ) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun <T : Any> getObject(key: String, clazz: KClass<T>): T? {
        return getString(key, String.EMPTY)?.takeIfNotBlank()?.fromJson(clazz)
    }

    fun <T : Any> putObject(key: String, obj: T?): Boolean {
        return edit().putString(key, obj?.toJson().orEmpty()).commit()
    }
}