package com.personal.accountantAssistant.ui.login

import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.common.SignInButton
import com.personal.accountantAssistant.R

@Composable
fun LoginScreen(
    isProcessing: Boolean,
    isSignInVisible: Boolean,
    onSignInClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.primaryColor)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AndroidView(
                factory = { ctx ->
                    android.widget.ImageView(ctx).apply {
                        setImageResource(R.mipmap.ic_launcher_round)
                    }
                },
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isProcessing) {
                CircularProgressIndicator(
                    color = colorResource(R.color.whiteColor),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isSignInVisible) {
                AndroidView(
                    factory = { ctx ->
                        SignInButton(ctx).apply {
                            setSize(SignInButton.SIZE_STANDARD)
                            setOnClickListener { onSignInClick() }
                        }
                    },
                    modifier = Modifier.wrapContentSize()
                )
            }
        }
    }
}