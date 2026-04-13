package com.personal.accountantAssistant.ui.scanner

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.ui.theme.Dimens

/**
 * Full-screen scanner overlay:
 *  • Transparent scan window
 *  • White corner brackets framing the target area
 *  • Animated cyan scan line giving visual feedback that the camera is active
 *  • Hint label below the window
 *  • "Manual entry" button at the bottom
 */
@Composable
internal fun ScannerOverlay(mode: ScanMode, onSkip: () -> Unit, modifier: Modifier = Modifier) {

    // Normalized (widthFraction, heightFraction) of the scan area, centered on screen.
    // Drives both the visual overlay and the ROI crop in the image analyzer.
    val wf = mode.windowWidthFraction
    val hf = mode.windowHeightFraction

    val infiniteTransition = rememberInfiniteTransition(label = "scanner_overlay")

    val lineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan_line"
    )

    BoxWithConstraints(modifier.fillMaxSize()) {
        val hintTopPadding = maxHeight * (1f + hf) / 2f + Dimens.spacingSm

        Canvas(Modifier.fillMaxSize()) {
            val bracketClr = Color.White
            val lineClr = Color(0xFF00E5FF).copy(alpha = 0.85f)

            val rL = size.width * (1f - wf) / 2f
            val rT = size.height * (1f - hf) / 2f
            val rR = size.width * (1f + wf) / 2f
            val rB = size.height * (1f + hf) / 2f

            // Corner brackets — L-shaped lines at each corner
            val bl = 36.dp.toPx()
            val bw = 3.5.dp.toPx()
            val cap = StrokeCap.Round

            // Top-left
            drawLine(bracketClr, Offset(rL, rT + bl), Offset(rL, rT), bw, cap)
            drawLine(bracketClr, Offset(rL, rT), Offset(rL + bl, rT), bw, cap)
            // Top-right
            drawLine(bracketClr, Offset(rR - bl, rT), Offset(rR, rT), bw, cap)
            drawLine(bracketClr, Offset(rR, rT), Offset(rR, rT + bl), bw, cap)
            // Bottom-left
            drawLine(bracketClr, Offset(rL, rB - bl), Offset(rL, rB), bw, cap)
            drawLine(bracketClr, Offset(rL, rB), Offset(rL + bl, rB), bw, cap)
            // Bottom-right
            drawLine(bracketClr, Offset(rR - bl, rB), Offset(rR, rB), bw, cap)
            drawLine(bracketClr, Offset(rR, rB), Offset(rR, rB - bl), bw, cap)

            // Animated horizontal scan line
            val lY = rT + (rB - rT) * lineProgress
            drawLine(lineClr, Offset(rL + bl, lY), Offset(rR - bl, lY), strokeWidth = 2.dp.toPx())
        }

        // Hint text — appears just below the scan window
        Surface(
            color = Color.Black.copy(alpha = 0.55f),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = hintTopPadding)
        ) {
            Text(
                text = stringResource(mode.hintRes),
                color = Color.White,
                fontSize = Dimens.textMd,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    horizontal = Dimens.spacingMd,
                    vertical = Dimens.spacingSm
                )
            )
        }

        // Manual-entry button at the bottom
        Button(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(Dimens.spacingMd)
                .height(Dimens.buttonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            )
        ) {
            Text(
                text = stringResource(R.string.scan_manual_entry),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}