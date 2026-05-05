package com.personal.accountantAssistant.ui.scanner.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.ui.common.CircleIconButton
import com.personal.accountantAssistant.ui.common.RoundedTextButton
import com.personal.accountantAssistant.ui.scanner.ScanCodeType
import com.personal.accountantAssistant.ui.scanner.ScannerOverlay
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import com.personal.accountantAssistant.ui.theme.Dimens

@Composable
internal fun ScannerPreviewContent(scanCodeType: ScanCodeType = ScanCodeType.QR_CODE) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1C1E))
    ) {
        ScannerOverlay(
            modifier = Modifier.fillMaxSize(),
            type = scanCodeType
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(Dimens.spacingMd),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleIconButton(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        stringResourceId = R.string.back_action,
                        onClick = {}
                    )
                    RoundedTextButton(
                        isSelected = scanCodeType.isQrCode(),
                        stringResourceId = ScanCodeType.QR_CODE.textRes,
                        onClick = {}
                    )
                    RoundedTextButton(
                        isSelected = scanCodeType.isBarcode(),
                        stringResourceId = ScanCodeType.BARCODE.textRes,
                        onClick = {}
                    )
                }
                Text(
                    text = stringResource(R.string.scan_bill_hint),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.buttonHeight),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.scan_manual_entry_text),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_9_PRO)
@Composable
fun ScannerQrPreview() {
    AccountantTheme { ScannerPreviewContent(ScanCodeType.QR_CODE) }
}

@Preview(showBackground = true, device = Devices.PIXEL_9_PRO)
@Composable
fun ScannerBarcodePreview() {
    AccountantTheme { ScannerPreviewContent(ScanCodeType.BARCODE) }
}