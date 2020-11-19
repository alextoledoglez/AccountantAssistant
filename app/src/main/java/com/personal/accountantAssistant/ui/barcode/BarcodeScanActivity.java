package com.personal.accountantAssistant.ui.barcode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.personal.accountantAssistant.R;
import com.personal.accountantAssistant.utils.Constants;
import com.personal.accountantAssistant.utils.ParserUtils;
import com.personal.accountantAssistant.utils.PermissionsUtils;

import java.io.IOException;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BarcodeScanActivity extends AppCompatActivity {

    private Context context;

    private SurfaceView cameraPreviewSurface;
    private EditText barcodeScanNumber;
    private CameraSource cameraSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);
        Objects.requireNonNull(getSupportActionBar())
                .setTitle(R.string.barcode_scanner);

        context = BarcodeScanActivity.this;

        cameraPreviewSurface = findViewById(R.id.bar_code_scan_view);
        barcodeScanNumber = findViewById(R.id.bar_code_scan_number);
        /*final Button barcodeScanButton = findViewById(R.id.bar_code_scan_button);
        barcodeScanButton.setOnClickListener(view -> finish());*/

        createCameraSource();
    }

    public void createCameraSource() {

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.PRODUCT).build();

        cameraSource = new CameraSource.Builder(context, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(Boolean.TRUE)
                .setRequestedPreviewSize(displayMetrics.widthPixels, displayMetrics.heightPixels)
                .setRequestedFps(24.0f)
                .build();

        cameraPreviewSurface
                .getHolder()
                .addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder surfaceHolder) {
                        cameraSourceStartFrom(surfaceHolder);
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                        cameraSource.stop();
                    }
                });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barCodes = detections.getDetectedItems();
                if (barCodes.size() > 0) {
                    final Barcode barcode = barCodes.valueAt(0);
                    barcodeScanNumber.setText(barcode.displayValue);
                    //TODO something
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void cameraSourceStartFrom(final SurfaceHolder surfaceHolder) {
        try {
            if (!PermissionsUtils.isCameraPermissionGranted(context)) return;
            this.cameraSource.start(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
