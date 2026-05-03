package com.personal.accountantAssistant.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.personal.accountantAssistant.extensions.closeApp
import com.personal.accountantAssistant.ui.theme.AccountantTheme

class MainActivity : AppCompatActivity() {

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            closeApp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        setContent { AccountantTheme { MainScreen() } }
    }

    companion object {
        fun startActivity(context: Context) {
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(this)
            }
        }
    }
}