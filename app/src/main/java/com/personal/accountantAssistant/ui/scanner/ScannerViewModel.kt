package com.personal.accountantAssistant.ui.scanner

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.domain.useCases.bills.GetCompanyNameUseCase
import com.personal.accountantAssistant.domain.useCases.buys.GetBarcodeProductNameUseCase
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.ui.scanner.parser.ScannedCodeData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class ScannerViewModel(
    private val getBarcodeProductNameUseCase: GetBarcodeProductNameUseCase,
    private val getCompanyNameUseCase: GetCompanyNameUseCase,
    analytics: AnalyticsProvider? = null
) : BaseViewModel(analytics) {

    private val _isParsing = AtomicBoolean(false)

    private val _scanResult = MutableSharedFlow<ScanResult>(extraBufferCapacity = 1)
    val scanResult: SharedFlow<ScanResult> = _scanResult.asSharedFlow()

    private fun ScannedCodeData.toScanResult(): ScanResult? = when (this.scanMode) {
        ScanMode.BUY -> ScanResult.Buy(barcode, name, value, confidence, rawText)
        ScanMode.BILL -> ScanResult.Bill(barcode, name, value, date, confidence, rawText)
    }

    fun onBarcodeDetected(scannedData: ScannedCodeData?) {
        if (!_isParsing.compareAndSet(false, true)) return
        viewModelScope.launch {
            val enrichedScannedData = when (scannedData?.scanMode) {
                ScanMode.BUY -> {
                    val productName = scannedData.name.ifBlank {
                        getBarcodeProductNameUseCase(scannedData.barcode)
                    }
                    scannedData.copy(name = productName)
                }

                ScanMode.BILL -> {
                    val billName = scannedData.name.ifBlank {
                        getCompanyNameUseCase(scannedData.segment, scannedData.company)
                    }
                    scannedData.copy(name = billName)
                }

                else -> null
            }

            Log.i(TAG, "enrichedScannedData: $enrichedScannedData")
            val result = enrichedScannedData?.toScanResult()

            if (result != null) {
                _scanResult.emit(result)
            } else {
                _isParsing.set(false)
            }
        }
    }

    fun resetDetection() {
        _isParsing.set(false)
    }

    companion object {
        private val TAG = ScannerViewModel::class.java.simpleName
    }
}