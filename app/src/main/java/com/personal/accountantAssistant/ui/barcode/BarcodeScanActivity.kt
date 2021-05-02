package com.personal.accountantAssistant.ui.barcode

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.utils.PermissionsUtils.isCameraPermissionGranted
import java.io.IOException
import java.util.*

class BarcodeScanActivity : AppCompatActivity() {

    private var context: Context? = null
    private var cameraPreviewSurface: SurfaceView? = null
    private var barcodeScanNumber: EditText? = null
    private var cameraSource: CameraSource? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode)
        supportActionBar?.setTitle(R.string.barcode_scanner)
        context = this@BarcodeScanActivity
        cameraPreviewSurface = findViewById(R.id.bar_code_scan_view)
        barcodeScanNumber = findViewById(R.id.bar_code_scan_number)
        /*final Button barcodeScanButton = findViewById(R.id.bar_code_scan_button);
        barcodeScanButton.setOnClickListener(view -> finish());*/createCameraSource()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun createCameraSource() {
        val displayMetrics = DisplayMetrics()

        context?.display?.getRealMetrics(displayMetrics)
        val barcodeDetector = BarcodeDetector.Builder(this@BarcodeScanActivity)
                .setBarcodeFormats(Barcode.PRODUCT).build()
        cameraSource = CameraSource.Builder(this@BarcodeScanActivity, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(displayMetrics.widthPixels, displayMetrics.heightPixels)
                .setRequestedFps(24.0f)
                .build()
        cameraPreviewSurface?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                cameraSourceStartFrom(surfaceHolder)
            }

            override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {}
            override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
                cameraSource?.stop()
            }
        })
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}
            override fun receiveDetections(detections: Detections<Barcode>) {
                val barCodes = detections.detectedItems
                if (barCodes.size() > 0) {
                    val barcode = barCodes.valueAt(0)
                    barcodeScanNumber?.setText(barcode.displayValue)
                    //TODO something
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun cameraSourceStartFrom(surfaceHolder: SurfaceHolder) {
        try {
            if (!isCameraPermissionGranted(this@BarcodeScanActivity)) return
            cameraSource?.start(surfaceHolder)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}