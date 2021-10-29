package com.personal.accountantAssistant.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.personal.accountantAssistant.ui.MainActivity

object ActivityUtils {

    @JvmStatic
    fun startMainActivity(packageContext: Context) {
        Intent(packageContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            packageContext.startActivity(this)
        }
    }

    @JvmStatic
    fun startActivity(
        packageContext: Context,
        activityClass: Class<*>?
    ) {
        Intent(packageContext, activityClass).apply {
            packageContext.startActivity(this)
        }
    }

    @JvmStatic
    fun parse(context: Context): Activity {
        return context as Activity
    }

    @JvmStatic
    fun parse(activity: Activity?): Context? {
        return activity?.applicationContext
    }
}