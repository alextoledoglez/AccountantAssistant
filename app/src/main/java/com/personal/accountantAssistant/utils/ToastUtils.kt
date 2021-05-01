package com.personal.accountantAssistant.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {
    fun showLongText(context: Context?,
                     text: String?) {
        Toast.makeText(context,
                text,
                Toast.LENGTH_LONG)
                .show()
    }

    @JvmStatic
    fun showLongText(context: Context?,
                     resId: Int) {
        Toast.makeText(context,
                resId,
                Toast.LENGTH_LONG)
                .show()
    }

    fun showShortText(context: Context?,
                      text: String?) {
        Toast.makeText(context,
                text,
                Toast.LENGTH_SHORT)
                .show()
    }

    fun showShortText(context: Context?,
                      resId: Int) {
        Toast.makeText(context,
                resId,
                Toast.LENGTH_SHORT)
                .show()
    }
}