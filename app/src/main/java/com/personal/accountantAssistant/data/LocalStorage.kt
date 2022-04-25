package com.personal.accountantAssistant.data

import android.content.Context
import android.content.SharedPreferences
import com.personal.accountantAssistant.extensions.fromJson
import com.personal.accountantAssistant.extensions.toJson
import com.personal.accountantAssistant.extensions.toSharedPreferences
import kotlin.reflect.KClass

class LocalStorage(val context: Context) : SharedPreferences {

    private val preferences = context.toSharedPreferences()

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
        return getString(key, null)?.fromJson(clazz)
    }

    fun <T : Any> putObject(key: String, obj: T) {
        edit().putString(key, obj.toJson()).apply()
    }

    companion object {
        const val AVAILABLE_MONEY = "available_money"
        const val FIRST_STR_DATE = "first_str_date"
        const val LAST_STR_DATE = "last_str_date"
        const val SIGNED_USER = "signed_user"
    }
}