package com.personal.accountantAssistant.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object PermissionsUtils {
    @JvmStatic
    fun isCameraPermissionGranted(context: Context): Boolean {
        return checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    fun isCalendarWritePermissionGranted(context: Context): Boolean {
        return checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkSelfPermission(context: Context,
                                    permission: String): Int {
        return ActivityCompat.checkSelfPermission(context, permission)
    }
}