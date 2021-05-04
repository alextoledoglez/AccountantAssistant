package com.personal.accountantAssistant.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.SignInButton
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.services.SignInService

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val ACCOUNT_NAME_SIGN_IN_REQUEST_CODE = 1
        private const val CHOOSER_SIGN_IN_REQUEST_CODE = 2
        private val TAG = LoginActivity::class.java.simpleName
    }

    private var signInService: SignInService? = null
    private var signInButton: SignInButton? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        signInButton = findViewById<SignInButton>(R.id.sign_in_button).apply {
            setSize(SignInButton.SIZE_WIDE)
            setOnClickListener { signInService(CHOOSER_SIGN_IN_REQUEST_CODE) }
        }
        progressBar = findViewById(R.id.progress_bar)
    }

    override fun onResume() {
        super.onResume()
        initializeSignInService()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressBar?.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode == RESULT_OK) {
            resultData?.let { signInService?.handleSignInResult(it, signInButton, progressBar) }
        } else {
            progressBar?.visibility = View.GONE
            Log.d(TAG, getString(R.string.sign_in_canceled))
        }
    }

    private fun initializeSignInService() {
        signInService = signInService ?: run { SignInService(applicationContext) }
        signInService(ACCOUNT_NAME_SIGN_IN_REQUEST_CODE)
    }

    private fun signInService(signInRequestCode: Int) {
        signInService?.let { service ->
            progressBar?.visibility = View.VISIBLE
            when (signInRequestCode) {
                ACCOUNT_NAME_SIGN_IN_REQUEST_CODE -> {
                    signInButton?.visibility = View.INVISIBLE
                    service.requestAccountNameSignIn()
                }
                else -> service.requestSignIn()
            }.also { startActivityForResult(it, signInRequestCode) }
        } ?: run {
            progressBar?.visibility = View.GONE
        }
    }
}