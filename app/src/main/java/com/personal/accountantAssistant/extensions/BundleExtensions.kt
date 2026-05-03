package com.personal.accountantAssistant.extensions

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.BundleCompat.getParcelable

inline fun <reified T : Parcelable> Bundle.getParcelableCompat(key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(this, key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(key)
    }