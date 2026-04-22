package com.personal.accountantAssistant.ui.scanner

import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.common.Barcode
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.domain.useCases.bills.GetCompanyNameUseCase
import com.personal.accountantAssistant.domain.useCases.buys.GetBarcodeProductNameUseCase
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.ui.scanner.parser.BarcodeParser.isBarcodeFormat
import com.personal.accountantAssistant.ui.scanner.parser.BarcodeParser.toScannedCodeData
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

    fun onBarcodeDetected(scanMode: ScanMode, barcode: Barcode) {
        if (!_isParsing.compareAndSet(false, true)) return
        viewModelScope.launch {
            val scannedData = barcode.toScannedCodeData()
            val isBarcodeWitBlankName = barcode.isBarcodeFormat() && scannedData.name.isBlank()
            val enrichedScannedData = when (scanMode) {
                ScanMode.BUY if isBarcodeWitBlankName -> {
                    val productName = getBarcodeProductNameUseCase(scannedData.barcode)
                    scannedData.copy(name = productName.orEmpty())
                }

                ScanMode.BILL if isBarcodeWitBlankName -> {
                    val billName = getCompanyNameUseCase(scannedData.segment, scannedData.company)
                    scannedData.copy(name = billName)
                }

                else -> scannedData
            }

            val result = when (scanMode) {
                ScanMode.BUY -> enrichedScannedData.toBuyScanResult()
                ScanMode.BILL -> enrichedScannedData.toBillScanResult()
            }

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
}