package com.personal.accountantAssistant.extensions

import android.Manifest
import kotlin.reflect.KClass

val KClass<Manifest>.STORAGE_PERMISSIONS: Array<String>
    get() = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )