package com.personal.accountantAssistant.utils

import android.content.Context
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.personal.accountantAssistant.R

object BarcodeScanUtils {
    fun scanFrom(context: Context): String {
        val result = Constants.EMPTY_STR
        val detector = BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.CODABAR or Barcode.QR_CODE)
                .build()
        if (!detector.isOperational) {
            ToastUtils.showShortText(context, R.string.fail_bar_code_detector)
        } else {
            /*           final SparseArray<Barcode> barCodes = detector.detect(new Frame());
            final Barcode thisCode = barCodes.valueAt(0);
            result = thisCode.rawValue;*/
        }
        return result
    }
}