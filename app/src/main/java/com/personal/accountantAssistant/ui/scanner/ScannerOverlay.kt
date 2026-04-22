package com.personal.accountantAssistant.ui.scanner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.personal.accountantAssistant.ui.scanner.camera.ScanWindow

/**
 * Full-screen scanner overlay:
 *  • Transparent scan window
 *  • White corner brackets framing the target area
 *  • Animated cyan scan line giving visual feedback that the camera is active
 *  • Hint label below the window
 *  • "Manual entry" button at the bottom
 */
@Composable
internal fun ScannerOverlay(
    modifier: Modifier = Modifier,
    type: ScanCodeType,
    overlayColor: Color = Color.Black.copy(alpha = 0.55f),
    frameColor: Color = Color(0xFF6EE7F2)
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val scanWindow = ScanWindow.calculateScanWindow(type, size.width, size.height)

        drawRect(color = overlayColor)

        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(scanWindow.left, scanWindow.top),
            size = Size(scanWindow.width, scanWindow.height),
            cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
            blendMode = BlendMode.Clear
        )

        drawRoundRect(
            color = frameColor.copy(alpha = 0.20f),
            topLeft = Offset(scanWindow.left, scanWindow.top),
            size = Size(scanWindow.width, scanWindow.height),
            cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
        )

        drawRoundRect(
            color = frameColor.copy(alpha = 0.25f),
            topLeft = Offset(scanWindow.left, scanWindow.top),
            size = Size(scanWindow.width, scanWindow.height),
            cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )

        if (type == ScanCodeType.BARCODE) {
            drawLine(
                color = frameColor.copy(alpha = 0.90f),
                start = Offset(size.width / 2f, scanWindow.top + 18.dp.toPx()),
                end = Offset(size.width / 2f, scanWindow.bottom - 18.dp.toPx()),
                strokeWidth = 3.dp.toPx()
            )
        }
    }
}