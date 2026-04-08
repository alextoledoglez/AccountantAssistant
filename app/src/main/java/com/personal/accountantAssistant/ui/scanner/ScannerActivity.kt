package com.personal.accountantAssistant.ui.scanner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.personal.accountantAssistant.ui.theme.AccountantTheme

class ScannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val scanMode = ScanMode.valueOf(intent.getStringExtra(EXTRA_MODE).orEmpty())

        setContent {
            AccountantTheme {
                ScannerScreen(
                    mode = scanMode,
                    onResult = { result ->
                        val data = Intent().putExtra(EXTRA_RESULT, result)
                        setResult(RESULT_OK, data)
                        finish()
                    },
                    onSkip = { finish() }
                )
            }
        }
    }

    companion object {
        const val EXTRA_MODE = "extra_scan_mode"
        const val EXTRA_RESULT = "extra_scan_result"

        fun newIntent(context: Context, scanMode: ScanMode): Intent {
            return Intent(context, ScannerActivity::class.java)
                .putExtra(EXTRA_MODE, scanMode.name)
        }
    }
}