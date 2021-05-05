package com.personal.accountantAssistant.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.google.android.gms.common.SignInButton
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.ui.SignInActivity

class LoginActivity : SignInActivity() {

    companion object {
        const val ACCOUNT_NAME_SIGN_IN_REQUEST_CODE = 1
        const val CHOOSER_SIGN_IN_REQUEST_CODE = 2
        private val TAG = LoginActivity::class.java.simpleName
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        progressBar = findViewById(R.id.progress_bar)
        signInButton = findViewById<SignInButton>(R.id.sign_in_button).apply {
            setOnClickListener {
                progressBar?.visibility = View.VISIBLE
                signInService?.requestSignIn()?.let { startActivityForResult(it, CHOOSER_SIGN_IN_REQUEST_CODE) }
                        ?: run { progressBar?.visibility = View.GONE }
            }
        }
        signInService?.signOut()
    }

    override fun onResume() {
        super.onResume()
        signInService?.let { service ->
            progressBar?.visibility = View.VISIBLE
            service.requestAccountNameSignIn()?.let { startActivityForResult(it, ACCOUNT_NAME_SIGN_IN_REQUEST_CODE) }
                    ?: run {
                        signInButton?.visibility = View.VISIBLE
                        progressBar?.visibility = View.GONE
                    }
        }
    }
}