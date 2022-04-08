package com.personal.accountantAssistant.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.ui.MainActivity

fun Context.startMainActivity() {
    Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(this)
    }
}

fun Context.startActivity(activityClass: Class<*>?) {
    Intent(this, activityClass).apply { startActivity(this) }
}

fun Context.toActivity() = this as Activity

fun Context.toMainActivity() = this as MainActivity

fun Context.getCompatColor(@ColorRes resColor: Int) = ContextCompat.getColor(this, resColor)

fun Context.getCompatColor(
    condition: Boolean?, @ColorRes trueResColor: Int, @ColorRes falseResColor: Int
) = getCompatColor(if (condition.orFalse()) trueResColor else falseResColor)

fun Context.showToastLongText(text: String?) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun Context.showToastLongText(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_LONG).show()
}

fun Context.showToastShortText(text: String?) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.showToastShortText(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun Context.scan(): String {
    val result = String.EMPTY
    val detector = BarcodeDetector.Builder(this)
        .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.CODABAR or Barcode.QR_CODE)
        .build()
    if (!detector.isOperational) {
        this.showToastShortText(R.string.fail_bar_code_detector)
    } else {
        /*           final SparseArray<Barcode> barCodes = detector.detect(new Frame());
        final Barcode thisCode = barCodes.valueAt(0);
        result = thisCode.rawValue;*/
    }
    return result
}
