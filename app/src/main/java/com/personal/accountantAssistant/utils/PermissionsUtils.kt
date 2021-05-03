package com.personal.accountantAssistant.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object PermissionsUtils {
    var STORAGE_PERMISSION_CODE = 2
    var STORAGE_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun haveStoragePermissionGranted(context: Context): Boolean {
        return hasPermissionGranted(context, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                hasPermissionGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun hasPermissionGranted(context: Context, permission: String): Boolean {
        val checkSelfPermission: Int = ActivityCompat.checkSelfPermission(context, permission)
        return checkSelfPermission == PackageManager.PERMISSION_GRANTED
    }

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